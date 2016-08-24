package com.capslock.im.cluster;

import com.capslock.im.commons.model.ServerPeer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by capslock1874.
 */
@Component
public class LogicServerNodeSelector extends ServerNodeSelector {
    @Autowired
    private LogicServerClusterManager logicServerClusterManager;

    @Override
    public List<ServerPeer> getNodeList() {
        return logicServerClusterManager.getServerList();
    }
}
