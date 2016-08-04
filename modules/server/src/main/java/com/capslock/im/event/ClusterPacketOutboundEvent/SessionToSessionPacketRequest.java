package com.capslock.im.event.ClusterPacketOutboundEvent;

import com.capslock.im.commons.model.ClientPeer;
import com.capslock.im.commons.packet.AbstractSocketPacket;
import com.capslock.im.commons.packet.cluster.PacketType;
import lombok.Getter;

/**
 * Created by capslock1874.
 */
@Getter
public final class SessionToSessionPacketRequest extends AbstractClusterPacketRequest {
    private final ClientPeer senderClient;
    private final long receiverUid;

    public SessionToSessionPacketRequest(final AbstractSocketPacket packet, final ClientPeer senderClient,
            final long receiverUid) {
        super(packet);
        this.senderClient = senderClient;
        this.receiverUid = receiverUid;
    }

    @Override
    public PacketType getType() {
        return PacketType.S2S;
    }

    @Override
    public int getDispatchIndex() {
        return (int) receiverUid;
    }
}
