package com.capslock.im.component.storage;

import com.capslock.im.commons.model.LogicServerPeer;
import com.capslock.im.commons.packet.cluster.ClusterPacket;
import com.capslock.im.commons.packet.cluster.PacketType;
import com.capslock.im.component.MessageQueueManager;
import com.capslock.im.config.StorageServerCondition;
import com.capslock.im.model.event.ClusterPacketInboundEvent.ClusterPacketInboundEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by capslock1874.
 */
@Component
@Conditional(StorageServerCondition.class)
public class StorageMessageQueueManager extends MessageQueueManager {
    private String storageServerQueueName;

    @Autowired
    private StorageManager storageManager;

    @Override
    public String getName() {
        return "storage-mq";
    }

    @Override
    public void processInboundMessage(final ClusterPacket clusterPacket) {
        if (clusterPacket.getType() == PacketType.ST2S) {
            publishMessageToLogicServerQueue(clusterPacket, (LogicServerPeer) clusterPacket.getTo());
        }
    }

    private void publishMessageToLogicServerQueue(final ClusterPacket message, final LogicServerPeer logicServerPeer) {
        try {
            channel.basicPublish("", getLogicServerQueueName(logicServerPeer), null,
                    objectMapper.writeValueAsBytes(message));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initQueue() throws IOException {
        channel.queueDeclare(getStorageServerName(), false, false, false, null);
    }

    private String getStorageServerName() {
        if (storageServerQueueName == null) {
            storageServerQueueName = getStorageServerQueueNamePrefix() + getLocalHost();
        }
        return storageServerQueueName;
    }

    @Override
    protected void processMessageFromMessageQueue(final ClusterPacket clusterPacket) {
        storageManager.postMessage(new ClusterPacketInboundEvent(clusterPacket));
    }
}
