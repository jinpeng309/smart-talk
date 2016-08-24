package com.capslock.im.model.event.ClusterPacketOutboundEvent;

import com.capslock.im.commons.packet.AbstractMessageWithDispatchIndex;
import com.capslock.im.commons.packet.AbstractSocketPacket;
import com.capslock.im.commons.packet.cluster.PacketType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by capslock1874.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public abstract class AbstractClusterPacketRequest extends AbstractMessageWithDispatchIndex {
    private final AbstractSocketPacket packet;
    public abstract PacketType getType();
}
