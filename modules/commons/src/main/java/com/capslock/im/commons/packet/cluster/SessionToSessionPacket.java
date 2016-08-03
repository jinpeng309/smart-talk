package com.capslock.im.commons.packet.cluster;

import com.capslock.im.commons.model.ClientPeer;
import com.capslock.im.commons.model.Peer;
import com.capslock.im.commons.packet.ProtocolPacket;
import com.capslock.im.commons.packet.protocol.ClusterProtocol;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created by capslock1874.
 */
@Data
public class SessionToSessionPacket extends Packet {
    @JsonProperty(ClusterProtocol.MESSAGE_FROM)
    private final ClientPeer messageFrom;
    @JsonProperty(ClusterProtocol.MESSAGE_TO)
    private final Long messageTo;

    public SessionToSessionPacket(final Peer from, final Peer to, final ProtocolPacket protocolPacket,
            final ClientPeer messageFrom, final Long messageTo) {
        super(from, to, protocolPacket);
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
