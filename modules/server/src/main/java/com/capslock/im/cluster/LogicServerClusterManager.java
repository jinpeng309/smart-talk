package com.capslock.im.cluster;

import com.capslock.im.commons.model.LogicServerPeer;
import com.capslock.im.commons.model.ServerPeer;
import com.google.common.eventbus.EventBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Created by capslock1874.
 */

@Component
public class LogicServerClusterManager extends AbstractServerClusterManager {
    private static final String logicServerPath = "/talk/logic";
    @Autowired
    @Qualifier("logicServerClusterEventBus")
    private EventBus logicServerCusterEventBus;

    @Override
    ServerPeer createServerPeer(final String serverIp) {
        return new LogicServerPeer(serverIp);
    }

    @Override
    public EventBus getEventBus() {
        return logicServerCusterEventBus;
    }

    @Override
    public String getServerPath() {
        return logicServerPath;
    }
}
