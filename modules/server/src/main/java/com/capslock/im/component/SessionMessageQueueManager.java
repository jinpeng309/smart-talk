package com.capslock.im.component;

import com.capslock.im.commons.deserializer.ClusterPacketDeserializer;
import com.capslock.im.commons.model.ClientPeer;
import com.capslock.im.commons.model.ConnServerPeer;
import com.capslock.im.commons.model.LogicServerPeer;
import com.capslock.im.commons.packet.cluster.Packet;
import com.capslock.im.commons.packet.cluster.PacketType;
import com.capslock.im.config.LogicServerCondition;
import com.capslock.im.event.ClusterPacketInboundEvent.ClusterPacketInboundEvent;
import com.google.common.base.Charsets;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by capslock1874.
 */
@Component
@Conditional(LogicServerCondition.class)
public class SessionMessageQueueManager extends MessageQueueManager {
    private String logicServerQueueName;
    private ConcurrentHashMap<String, String> connServerNameMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, String> logicServerNameMap = new ConcurrentHashMap<>();

    @Autowired
    private SessionManager sessionManager;

    @Override
    protected void initQueue() throws IOException {
        channel.queueDeclare(getLogicServerName(), false, false, false, null);
        addLogicServerQueueConsumer();
    }

    private String getLogicServerQueueName(final LogicServerPeer logicServerPeer) {
        String queueName = logicServerNameMap.get(logicServerPeer.getServerIp());
        if (queueName == null) {
            queueName = getLogicServerQueueNamePrefix() + "_" + logicServerPeer.getServerIp();
            logicServerNameMap.put(logicServerPeer.getServerIp(), queueName);
        }
        return queueName;
    }

    private String getConnServerQueueName(final ConnServerPeer logicServerPeer) {
        String queueName = connServerNameMap.get(logicServerPeer.getServerIp());
        if (queueName == null) {
            queueName = getConnServerQueueNamePrefix() + logicServerPeer.getServerIp();
            connServerNameMap.put(logicServerPeer.getServerIp(), queueName);
        }
        return queueName;
    }

    private void addLogicServerQueueConsumer() throws IOException {
        final Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(final String consumerTag, final Envelope envelope,
                    final AMQP.BasicProperties properties, final byte[] body) throws IOException {
                final String rawData = new String(body, Charsets.UTF_8);
                final Packet packet = ClusterPacketDeserializer.deserialize(rawData);
                processMessageFromMessageQueue(packet);
            }
        };
        channel.basicConsume(getLogicServerName(), true, consumer);
    }

    private String getLogicServerName() {
        if (logicServerQueueName == null) {
            logicServerQueueName = getLogicServerQueueNamePrefix() + getLocalHost();
        }
        return logicServerQueueName;
    }

    @Override
    protected void processMessageFromMessageQueue(final Packet packet) {
        sessionManager.postMessage(new ClusterPacketInboundEvent(packet));
    }

    @Override
    public String getName() {
        return "sm-mq";
    }

    @Override
    public void processInboundMessage(final Packet packet) {
        if (packet.getType() == PacketType.S2C) {
            final ClientPeer clientPeer = (ClientPeer) packet.getTo();
            final ConnServerPeer connServerPeer = new ConnServerPeer(clientPeer.getConnServerNodeIp());
            publishMessageToConnServerQueue(packet, connServerPeer);
        } else if (packet.getType() == PacketType.S2S) {
            publishMessageToLogicServerQueue(packet, (LogicServerPeer) packet.getTo());
        }
    }

    private void publishMessageToLogicServerQueue(final Packet message, final LogicServerPeer logicServerPeer) {
        try {
            channel.basicPublish("", getLogicServerQueueName(logicServerPeer), null,
                    objectMapper.writeValueAsBytes(message));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void publishMessageToConnServerQueue(final Packet message, final ConnServerPeer connServerPeer) {
        try {
            channel.basicPublish("", getConnServerQueueName(connServerPeer), null,
                    objectMapper.writeValueAsBytes(message));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
