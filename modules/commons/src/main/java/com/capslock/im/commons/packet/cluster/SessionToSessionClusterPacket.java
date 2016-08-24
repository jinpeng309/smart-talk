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
public class SessionToSessionClusterPacket extends ClusterPacket {
    @JsonProperty(ClusterProtocol.MESSAGE_FROM)
    private final ClientPeer messageFrom;
    @JsonProperty(ClusterProtocol.MESSAGE_TO)
    private final Long messageTo;

    public SessionToSessionClusterPacket(final Peer from, final Peer to, final AbstractSocketPacket packet,
            final ClientPeer messageFrom, final Long messageTo) {
        super(from, to, packet.getProtocolName(), packet);
        this.messageFrom = messageFrom;
        this.messageTo = messageTo;
    }

    @Override
    public int getDispatchIndex() {
        return messageTo.intValue();
    }

    @Override
    public PacketType getType() {
        return PacketType.S2S;
    }
}
