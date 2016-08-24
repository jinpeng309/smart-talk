package com.capslock.im.model.event.rpcEvent;

import com.capslock.im.commons.packet.cluster.ClusterPacket;
import com.capslock.im.model.event.Event;
import com.capslock.im.model.event.EventType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by capslock1874.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ClusterPacketRpcEvent extends Event {
    private final ClusterPacket clusterPacket;

    @Override
    public int getDispatchIndex() {
        return clusterPacket.getDispatchIndex();
    }

    @Override
    public EventType getType() {
        return EventType.RPC;
    }
}
