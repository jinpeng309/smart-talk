package com.capslock.im.commons.packet.outbound;

import com.capslock.im.commons.annotations.Protocol;
import com.capslock.im.commons.packet.AbstractSocketPacket;
import com.capslock.im.commons.packet.protocol.AckProtocol;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by capslock1874.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Protocol(AckProtocol.NAME)
public class SocketAckResponse extends AbstractSocketPacket {
    @JsonProperty(AckProtocol.Outbound.UUID)
    private final String uuid;
    @JsonProperty(AckProtocol.Outbound.TO)
    private final long receiverUid;

    @Override
    public String getProtocolName() {
        return AckProtocol.NAME;
    }
}
