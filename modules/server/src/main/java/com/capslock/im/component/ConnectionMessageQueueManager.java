package com.capslock.im.component;

import com.capslock.im.commons.deserializer.ClusterPacketDeserializer;
import com.capslock.im.commons.model.LogicServerPeer;
import com.capslock.im.commons.packet.cluster.Packet;
import com.capslock.im.commons.packet.cluster.PacketType;
import com.capslock.im.config.ConnServerCondition;
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
@Conditional(ConnServerCondition.class)
public class ConnectionMessageQueueManager extends MessageQueueManager {
    @Autowired
    private ConnectionManager connectionManager;
    private String clientQueueName;
    private ConcurrentHashMap<String, String> logicServerNameMap = new ConcurrentHashMap<>();

    private String getClientQueueName() {
        if (clientQueueName == null) {
            clientQueueName = getConnServerQueueNamePrefix() + getLocalHost();
        }
        return clientQueueName;
    }

    private String getLogicServerQueueName(final LogicServerPeer logicServerPeer) {
        String queueName = logicServerNameMap.get(logicServerPeer.getServerIp());
        if (queueName == null) {
            queueName = getLogicServerQueueNamePrefix() + logicServerPeer.getServerIp();
            logicServerNameMap.put(logicServerPeer.getServerIp(), queueName);
        }
        return queueName;
    }

    @Override
    protected void initQueue() throws IOException {
        channel.queueDeclare(getConnServerQueueNamePrefix() + getLocalHost(), false, false, false, null);
        addClientQueueConsumer();
    }

    @Override
    protected void processMessageFromMessageQueue(final Packet packet) {
        if (packet.getType() == PacketType.S2C) {
            connectionManager.postMessage(packet);
        }
    }

    private void addClientQueueConsumer() throws IOException {
        final Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(final String consumerTag, final Envelope envelope,
                    final AMQP.BasicProperties properties, final byte[] body) throws IOException {
                final String rawData = new String(body, Charsets.UTF_8);
                final Packet packet = ClusterPacketDeserializer.deserialize(rawData);
                processMessageFromMessageQueue(packet);
            }
        };
        channel.basicConsume(getClientQueueName(), true, consumer);
    }

    @Override
    public String getName() {
        return "cm-mq";
    }

    @Override
    public void processInboundMessage(final Packet packet) {
        if (packet.getType() == PacketType.C2S) {
            publishMessageToQueue(packet, (LogicServerPeer) packet.getTo());
        }
    }

    private void publishMessageToQueue(final Packet message, final LogicServerPeer logicServerPeer) {
        try {
            channel.basicPublish("", getLogicServerQueueName(logicServerPeer), null,
                    objectMapper.writeValueAsBytes(message));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
