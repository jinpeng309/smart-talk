package com.capslock.im.commons.packet.cluster;

import com.capslock.im.commons.model.ClientPeer;
import com.capslock.im.commons.model.Peer;
import com.capslock.im.commons.packet.AbstractSocketPacket;
import com.capslock.im.commons.packet.protocol.ClusterProtocol;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created by capslock1874.
 */
@Data
public class SessionToStorageClusterPacket extends ClusterPacket {

    @JsonProperty(ClusterProtocol.MESSAGE_FROM)
    private final ClientPeer messageFrom;

    public SessionToStorageClusterPacket(final Peer from, final Peer to, final String protocolName,
            final AbstractSocketPacket packet, final ClientPeer messageFrom) {
        super(from, to, protocolName, packet);
        this.messageFrom = messageFrom;
    }

    @Override
    public int getDispatchIndex() {
        return (int) messageFrom.getUid();
    }

    @Override
    public PacketType getType() {
        return PacketType.S2ST;
    }
}
