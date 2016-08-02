package com.capslock.im.commons.packet.cluster;

import com.capslock.im.commons.model.Peer;
import com.capslock.im.commons.packet.AbstractMessageWithDispatchIndex;
import com.capslock.im.commons.packet.ProtocolPacket;
import com.capslock.im.commons.packet.protocol.ClusterProtocol;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created by capslock1874.
 */
@Data
public abstract class Packet extends AbstractMessageWithDispatchIndex {
    @JsonProperty(ClusterProtocol.PACKET_FROM)
    private final Peer from;

    @JsonProperty(ClusterProtocol.PACKET_TO)
    private final Peer to;

    @JsonProperty(ClusterProtocol.PACKET_DATA)
    private final ProtocolPacket protocolPacket;

    @JsonProperty(ClusterProtocol.PACKET_TYPE)
    public abstract PacketType getType();
}
