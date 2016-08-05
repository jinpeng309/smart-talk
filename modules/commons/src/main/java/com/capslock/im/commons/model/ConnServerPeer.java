package com.capslock.im.commons.model;

/**
 * Created by capslock1874.
 */
public class ConnServerPeer extends ServerPeer {

    public ConnServerPeer(final String serverIp) {
        super(serverIp);
    }

    @Override
    public PeerType getType() {
        return PeerType.CONN_SERVER;
    }
}

