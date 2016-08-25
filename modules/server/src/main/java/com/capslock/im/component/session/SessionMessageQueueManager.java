package com.capslock.im.component.session;

import com.capslock.im.commons.deserializer.ClusterPacketDeserializer;
import com.capslock.im.commons.model.ClientPeer;
import com.capslock.im.commons.model.ConnServerPeer;
import com.capslock.im.commons.model.LogicServerPeer;
import com.capslock.im.commons.model.StorageServerPeer;
import com.capslock.im.commons.packet.cluster.ClusterPacket;
import com.capslock.im.commons.packet.cluster.PacketType;
import com.capslock.im.component.MessageQueueManager;
import com.capslock.im.config.LogicServerCondition;
import com.capslock.im.model.event.ClusterPacketInboundEvent.ClusterPacketInboundEvent;
import com.capslock.im.model.event.rpcEvent.ClusterPacketRpcEvent;
import com.google.common.base.Charsets;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by capslock1874.
 */
@Component
@Conditional(LogicServerCondition.class)
public class SessionMessageQueueManager extends MessageQueueManager {
    private String logicServerQueueName;

    @Autowired
    private SessionManager sessionManager;

    @Override
    protected void initQueue() throws IOException {
        channel.queueDeclare(getLogicServerName(), false, false, false, null);
        addLogicServerQueueConsumer();
    }

    private void addLogicServerQueueConsumer() throws IOException {
        final Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(final String consumerTag, final Envelope envelope,
                    final AMQP.BasicProperties properties, final byte[] body) throws IOException {
                final String rawData = new String(body, Charsets.UTF_8);
                final ClusterPacket clusterPacket = ClusterPacketDeserializer.deserialize(rawData);
                processMessageFromMessageQueue(clusterPacket);
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
    protected void processMessageFromMessageQueue(final ClusterPacket clusterPacket) {
        final PacketType packetType = clusterPacket.getType();
        if (packetType == PacketType.C2S || packetType == PacketType.S2S) {
            sessionManager.postMessage(new ClusterPacketInboundEvent(clusterPacket));
        } else if (packetType == PacketType.ST2S) {
            sessionManager.postMessage(new ClusterPacketRpcEvent(clusterPacket));
        }
    }

    @Override
    public String getName() {
        return "sm-mq";
    }

    @Override
    public void processInboundMessage(final ClusterPacket clusterPacket) {
        final PacketType packetType = clusterPacket.getType();
        if (packetType == PacketType.S2C) {
            final ClientPeer clientPeer = (ClientPeer) clusterPacket.getTo();
            final ConnServerPeer connServerPeer = new ConnServerPeer(clientPeer.getConnServerNodeIp());
            publishMessageToConnServerQueue(clusterPacket, connServerPeer);
        } else if (packetType == PacketType.S2S) {
            publishMessageToLogicServerQueue(clusterPacket, (LogicServerPeer) clusterPacket.getTo());
        } else if (packetType == PacketType.S2ST) {
            publishMessageToStorageServerQueue(clusterPacket, (StorageServerPeer) clusterPacket.getTo());
        }
    }

    private void publishMessageToStorageServerQueue(final ClusterPacket clusterPacket, final StorageServerPeer to) {
        try {
            channel.basicPublish("", getStorageServerQueueName(to), null, objectMapper.writeValueAsBytes(clusterPacket));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void publishMessageToLogicServerQueue(final ClusterPacket clusterPacket, final LogicServerPeer logicServerPeer) {
        try {
            channel.basicPublish("", getLogicServerQueueName(logicServerPeer), null,
                    objectMapper.writeValueAsBytes(clusterPacket));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void publishMessageToConnServerQueue(final ClusterPacket message, final ConnServerPeer connServerPeer) {
        try {
            channel.basicPublish("", getConnServerQueueName(connServerPeer), null,
                    objectMapper.writeValueAsBytes(message));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
