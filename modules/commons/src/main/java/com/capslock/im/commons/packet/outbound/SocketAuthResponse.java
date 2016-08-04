package com.capslock.im.commons.packet.outbound;

import com.capslock.im.commons.annotations.Protocol;
import com.capslock.im.commons.packet.inbound.AbstractSocketPacket;
import com.capslock.im.commons.packet.protocol.AuthenticationProtocol;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created by capslock1874.
 */
@Data
@Protocol(AuthenticationProtocol.NAME)
public class SocketAuthResponse extends AbstractSocketPacket {
    @JsonProperty(AuthenticationProtocol.Outbound.RESULT)
    private final int resultCode;

    @Override
    public String getProtocolName() {
        return AuthenticationProtocol.NAME;
    }
}
