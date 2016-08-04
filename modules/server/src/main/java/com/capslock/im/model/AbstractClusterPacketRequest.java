package com.capslock.im.model;

import com.capslock.im.commons.packet.AbstractMessageWithDispatchIndex;
import com.capslock.im.commons.packet.ProtocolPacket;
import com.capslock.im.commons.packet.cluster.PacketType;
import lombok.Data;

/**
 * Created by capslock1874.
 */
@Data
public abstract class AbstractClusterPacketRequest extends AbstractMessageWithDispatchIndex {
    private final ProtocolPacket packet;
    public abstract PacketType getType();
}
