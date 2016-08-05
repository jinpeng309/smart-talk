package com.capslock.im.event.rpcEvent;

import com.capslock.im.commons.packet.cluster.ClusterPacket;
import com.capslock.im.event.Event;
import com.capslock.im.event.EventType;
import lombok.Data;

/**
 * Created by capslock1874.
 */
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
