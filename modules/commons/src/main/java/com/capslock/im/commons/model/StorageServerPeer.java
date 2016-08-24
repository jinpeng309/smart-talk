package com.capslock.im.commons.model;

/**
 * Created by capslock1874.
 */
public class StorageServerPeer extends ServerPeer {
    public StorageServerPeer() {
    }

    public StorageServerPeer(final String serverIp) {
        super(serverIp);
    }

    @Override
    public PeerType getType() {
        return PeerType.STORAGE_SERVER;
    }
}

