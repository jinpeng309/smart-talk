package com.capslock.im.cluster;

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
    public EventBus getEventBus() {
        return connServerClusterEventBus;
    }

    @Override
    public String getServerPath() {
        return connServerPath;
    }
}
