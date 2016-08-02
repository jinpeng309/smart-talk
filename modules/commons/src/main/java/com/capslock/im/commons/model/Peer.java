package com.capslock.im.commons.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by capslock1874.
 */
public abstract class Peer {
    @JsonIgnore
    abstract public PeerType getType();
}
