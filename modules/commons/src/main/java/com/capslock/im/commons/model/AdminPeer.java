package com.capslock.im.commons.model;

import lombok.Data;

/**
 * Created by capslock1874.
 */
@Data
public class AdminPeer extends Peer {
    private final String userName;

    @Override
    public PeerType getType() {
        return PeerType.ADMIN;
    }
}
