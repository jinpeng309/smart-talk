package com.capslock.im.model;

import com.capslock.im.commons.model.ClientPeer;
import com.capslock.im.commons.packet.ProtocolPacket;
import com.capslock.im.commons.packet.cluster.PacketType;
import lombok.Data;

/**
 * Created by capslock1874.
 */
@Data
public final class SessionToSessionPacketRequest extends AbstractClusterPacketRequest {
    private final ClientPeer senderClient;
    private final long receiverUid;
    private final ProtocolPacket packet;

    @Override
    public PacketType getType() {
        return PacketType.S2S;
    }
}
