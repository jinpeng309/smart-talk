package com.capslock.im.cluster;

import com.capslock.im.commons.model.StorageServerPeer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by capslock1874.
 */
@Component
public class StorageServerNodeSelector extends ServerNodeSelector<StorageServerPeer> {
    @Autowired
    private ClusterManager clusterManager;

    @Override
    public List<StorageServerPeer> getNodeList() {
        return clusterManager.getStorageServerList();
    }
}
