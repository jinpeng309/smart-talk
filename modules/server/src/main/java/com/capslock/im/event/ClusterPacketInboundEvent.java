package com.capslock.im.event;

import com.capslock.im.commons.packet.cluster.Packet;
import com.capslock.im.commons.packet.cluster.PacketType;
import lombok.Data;

/**
 * Created by capslock1874.
 */
@Data
public class ClusterPacketInboundEvent extends Event {
    private final Packet packet;

    public PacketType getPacketType() {
        return packet.getType();
    }

    @Override
    public EventType getType() {
        return EventType.CLUSTER_PACKET_INBOUND;
    }

    @Override
    public int getDispatchIndex() {
        return packet.getDispatchIndex();
    }
}
