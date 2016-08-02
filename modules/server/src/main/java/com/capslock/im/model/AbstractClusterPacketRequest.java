package com.capslock.im.model;

import com.capslock.im.commons.packet.cluster.PacketType;

/**
 * Created by capslock1874.
 */
public abstract class AbstractClusterPacketRequest {
    public abstract PacketType getType();
}
