package com.capslock.im.cluster;

import com.capslock.im.commons.model.LogicServerPeer;
import com.capslock.im.component.ComponentIfc;
import com.google.common.hash.Hashing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by capslock1874.
 */
@Component
public class LogicServerNodeSelector implements ComponentIfc {
    @Autowired
    private ClusterManager clusterManager;

    @PostConstruct
    @Override
    public void setup() throws Exception {
    }

    public LogicServerPeer selectByUid(final long uid) {
        return clusterManager.getLogicServerList().get(Hashing.consistentHash(uid, clusterManager.logicServerSize()));
    }

}
