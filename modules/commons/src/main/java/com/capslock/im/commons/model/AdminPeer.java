package com.capslock.im.commons.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by capslock1874.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AdminPeer extends Peer {
    private final String userName;

    @Override
    public PeerType getType() {
        return PeerType.ADMIN;
    }
}
