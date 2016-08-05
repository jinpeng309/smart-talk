package com.capslock.im.cluster;

import com.capslock.im.commons.model.LogicServerPeer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by capslock1874.
 */
@Component
public class LogicServerNodeSelector extends ServerNodeSelector<LogicServerPeer> {
    @Autowired
    private ClusterManager clusterManager;

    @Override
    public List<LogicServerPeer> getNodeList() {
        return clusterManager.getLogicServerList();
    }

}
