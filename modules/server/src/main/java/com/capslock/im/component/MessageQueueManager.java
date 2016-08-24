package com.capslock.im.component;

import com.capslock.im.commons.model.ConnServerPeer;
import com.capslock.im.commons.model.LogicServerPeer;
import com.capslock.im.commons.model.StorageServerPeer;
import com.capslock.im.commons.packet.cluster.ClusterPacket;
import com.capslock.im.commons.util.NetUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by capslock1874.
 */
@Component
public abstract class MessageQueueManager extends MessageReceiver<ClusterPacket> {
    protected static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new Jdk8Module());
    @Autowired
    protected Connection connection;
    protected Channel channel;
    protected String localHost;

    protected String getStorageServerQueueName(final StorageServerPeer storageServerPeer) {
        return getStorageServerQueueNamePrefix() + storageServerPeer.getServerIp();
    }

    protected String getLogicServerQueueName(final LogicServerPeer logicServerPeer) {
        return getLogicServerQueueNamePrefix() + logicServerPeer.getServerIp();
    }

    protected String getConnServerQueueName(final ConnServerPeer logicServerPeer) {
        return getConnServerQueueNamePrefix() + logicServerPeer.getServerIp();
    }

    abstract protected void initQueue() throws IOException;

    abstract protected void processMessageFromMessageQueue(final ClusterPacket clusterPacket);

    protected String getConnServerQueueNamePrefix() {
        return "cm_";
    }

    protected String getLogicServerQueueNamePrefix() {
        return "sm_";
    }

    protected String getStorageServerQueueNamePrefix() {
        return "storage_";
    }

    protected String getExchangeName() {
        return "talk";
    }

    protected String getLocalHost() {
        return localHost;
    }

    @Override
    public void setup() throws Exception {
        super.setup();
        localHost = NetUtils.getLocalHost();
        channel = connection.createChannel();
        initQueue();
    }

    @Override
    public SchedulerType getSchedulerType() {
        return SchedulerType.IO;
    }
}
