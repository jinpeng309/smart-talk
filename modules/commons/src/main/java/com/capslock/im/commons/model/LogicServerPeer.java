package com.capslock.im.commons.model;

/**
 * Created by capslock1874.
 */
public class LogicServerPeer extends ServerPeer {
    public LogicServerPeer(final String serverIp) {
        super(serverIp);
    }

    @Override
    public PeerType getType() {
        return PeerType.LOGIC_SERVER;
    }
}
