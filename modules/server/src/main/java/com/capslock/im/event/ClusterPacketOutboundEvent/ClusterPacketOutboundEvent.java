package com.capslock.im.event.ClusterPacketOutboundEvent;

import com.capslock.im.commons.packet.cluster.PacketType;
import com.capslock.im.event.Event;
import com.capslock.im.event.EventType;
import lombok.Data;

/**
 * Created by capslock1874.
 */
@Data
public class ClusterPacketOutboundEvent extends Event {
    private final AbstractClusterPacketRequest request;

    public PacketType getReuqestType() {
        return request.getType();
    }

    @Override
    public EventType getType() {
        return EventType.CLUSTER_PACKET_OUTBOUND;
    }

    @Override
    public int getDispatchIndex() {
        return request.getDispatchIndex();
    }
}
