package com.capslock.im.event.ClusterPacketOutboundEvent;

import com.capslock.im.commons.packet.AbstractMessageWithDispatchIndex;
import com.capslock.im.commons.packet.AbstractSocketPacket;
import com.capslock.im.commons.packet.cluster.PacketType;
import lombok.Data;

/**
 * Created by capslock1874.
 */
@Data
public abstract class AbstractClusterPacketRequest extends AbstractMessageWithDispatchIndex {
    private final AbstractSocketPacket packet;
    public abstract PacketType getType();
}
