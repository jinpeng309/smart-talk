package com.capslock.im.commons.packet.outbound.response;

import com.capslock.im.commons.annotations.Protocol;
import com.capslock.im.commons.packet.protocol.AuthenticationProtocol;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created by capslock1874.
 */
@Data
@Protocol(AuthenticationProtocol.NAME)
public class SocketOutboundAuth extends AbstractSocketOutboundPacket {
    @JsonProperty(AuthenticationProtocol.Outbound.RESULT)
    private final int resultCode;
}
