package com.capslock.im.cluster;

import com.capslock.im.commons.model.ConnServerPeer;
import com.capslock.im.commons.model.ServerPeer;
import com.google.common.eventbus.EventBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Created by capslock1874.
 */

@Component
public class ConnServerClusterManager extends AbstractServerClusterManager {
    private static final String connServerPath = "/talk/conn";
    @Autowired
    @Qualifier("connServerClusterEventBus")
    private EventBus connServerClusterEventBus;

    @Override
    ServerPeer createServerPeer(final String serverIp) {
        return new ConnServerPeer(serverIp);
    }

    @Override
    public EventBus getEventBus() {
        return connServerClusterEventBus;
    }

    @Override
    public String getServerPath() {
        return connServerPath;
    }
}
