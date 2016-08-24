package com.capslock.im.commons.packet.cluster;

import com.capslock.im.commons.model.ClientPeer;
import com.capslock.im.commons.model.Peer;
import com.capslock.im.commons.packet.AbstractSocketPacket;
import com.capslock.im.commons.packet.protocol.ClusterProtocol;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by capslock1874.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StorageToSessionClusterPacket extends ClusterPacket {

    @JsonProperty(ClusterProtocol.MESSAGE_TO)
    private final ClientPeer messageTo;

    public StorageToSessionClusterPacket(final Peer from, final Peer to, final String protocolName,
            final AbstractSocketPacket packet, final ClientPeer messageTo) {
        super(from, to, protocolName, packet);
        this.messageTo = messageTo;
    }

    @Override
    public int getDispatchIndex() {
        return (int) messageTo.getUid();
    }

    @Override
    public PacketType getType() {
        return PacketType.ST2S;
    }
}
