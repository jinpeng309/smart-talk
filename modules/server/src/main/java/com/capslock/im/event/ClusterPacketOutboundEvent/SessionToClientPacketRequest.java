package com.capslock.im.event.ClusterPacketOutboundEvent;

import com.capslock.im.commons.model.ClientPeer;
import com.capslock.im.commons.packet.AbstractSocketPacket;
import com.capslock.im.commons.packet.cluster.PacketType;
import lombok.Getter;

/**
 * Created by capslock1874.
 */
@Getter
public class SessionToClientPacketRequest extends AbstractClusterPacketRequest {
    private final ClientPeer to;

    public SessionToClientPacketRequest(final AbstractSocketPacket packet, final ClientPeer to) {
        super(packet);
        this.to = to;
    }

    @Override
    public PacketType getType() {
        return PacketType.S2C;
    }

    @Override
    public int getDispatchIndex() {
        return (int) to.getUid();
    }
}
