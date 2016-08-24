package com.capslock.im.event.ClusterPacketInboundEvent;

import com.capslock.im.commons.packet.cluster.ClusterPacket;
import com.capslock.im.commons.packet.cluster.PacketType;
import com.capslock.im.event.Event;
import com.capslock.im.event.EventType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by capslock1874.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ClusterPacketInboundEvent extends Event {
    private final ClusterPacket clusterPacket;

    public PacketType getPacketType() {
        return clusterPacket.getType();
    }

    @Override
    public EventType getType() {
        return EventType.CLUSTER_PACKET_INBOUND;
    }

    @Override
    public int getDispatchIndex() {
        return clusterPacket.getDispatchIndex();
    }
}
