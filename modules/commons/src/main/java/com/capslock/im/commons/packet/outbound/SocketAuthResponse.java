package com.capslock.im.commons.packet.outbound;

import com.capslock.im.commons.annotations.Protocol;
import com.capslock.im.commons.packet.AbstractSocketPacket;
import com.capslock.im.commons.packet.protocol.AuthenticationProtocol;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by capslock1874.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Protocol(AuthenticationProtocol.NAME)
public class SocketAuthResponse extends AbstractSocketPacket {
    @JsonProperty(AuthenticationProtocol.Outbound.RESULT)
    private final int resultCode;

    @Override
    public String getProtocolName() {
        return AuthenticationProtocol.NAME;
    }
}
