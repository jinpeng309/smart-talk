package com.capslock.im.component.connection;

import com.capslock.im.commons.deserializer.ClusterPacketDeserializer;
import com.capslock.im.commons.model.LogicServerPeer;
import com.capslock.im.commons.packet.cluster.ClusterPacket;
import com.capslock.im.commons.packet.cluster.PacketType;
import com.capslock.im.component.MessageQueueManager;
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

/**
 * Created by capslock1874.
 */
@Component
@Conditional(ConnServerCondition.class)
public class ConnectionMessageQueueManager extends MessageQueueManager {
    @Autowired
    private ConnectionManager connectionManager;
    private String clientQueueName;

    private String getClientQueueName() {
        if (clientQueueName == null) {
            clientQueueName = getConnServerQueueNamePrefix() + getLocalHost();
        }
        return clientQueueName;
    }

    @Override
    protected void initQueue() throws IOException {
        channel.queueDeclare(getConnServerQueueNamePrefix() + getLocalHost(), false, false, false, null);
        addClientQueueConsumer();
    }

    @Override
    protected void processMessageFromMessageQueue(final ClusterPacket clusterPacket) {
        if (clusterPacket.getType() == PacketType.S2C) {
            connectionManager.postMessage(clusterPacket);
        }
    }

    private void addClientQueueConsumer() throws IOException {
        final Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(final String consumerTag, final Envelope envelope,
                    final AMQP.BasicProperties properties, final byte[] body) throws IOException {
                final String rawData = new String(body, Charsets.UTF_8);
                final ClusterPacket clusterPacket = ClusterPacketDeserializer.deserialize(rawData);
                processMessageFromMessageQueue(clusterPacket);
            }
        };
        channel.basicConsume(getClientQueueName(), true, consumer);
    }

    @Override
    public String getName() {
        return "cm-mq";
    }

    @Override
    public void processInboundMessage(final ClusterPacket clusterPacket) {
        if (clusterPacket.getType() == PacketType.C2S) {
            publishMessageToQueue(clusterPacket, (LogicServerPeer) clusterPacket.getTo());
        }
    }

    private void publishMessageToQueue(final ClusterPacket message, final LogicServerPeer logicServerPeer) {
        try {
            channel.basicPublish("", getLogicServerQueueName(logicServerPeer), null,
                    objectMapper.writeValueAsBytes(message));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
