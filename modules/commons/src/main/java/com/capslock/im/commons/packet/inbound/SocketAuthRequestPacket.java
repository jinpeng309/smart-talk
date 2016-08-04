package com.capslock.im.commons.packet.inbound;

import com.capslock.im.commons.annotations.Protocol;
import com.capslock.im.commons.packet.protocol.AuthenticationProtocol;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * Created by capslock1874.
 */
@Protocol(AuthenticationProtocol.NAME)
@Getter
public class SocketAuthRequestPacket extends AbstractSocketPacket {
    private long senderUid;
    private String token;
    private int deviceType;
    private String deviceUuid;

    public SocketAuthRequestPacket(@JsonProperty(value = AuthenticationProtocol.Inbound.FROM, required = true) final long senderUid,
            @JsonProperty(value = AuthenticationProtocol.Inbound.TOKEN, required = true) final String token,
            @JsonProperty(value = AuthenticationProtocol.Inbound.DEVICE_TYPE, required = true) final int deviceType,
            @JsonProperty(value = AuthenticationProtocol.Inbound.DEVICE_UUID, required = true) final String deviceUuid) {
        this.senderUid = senderUid;
        this.token = token;
        this.deviceType = deviceType;
        this.deviceUuid = deviceUuid;
    }

    @Override
    public String getProtocolName() {
        return AuthenticationProtocol.NAME;
    }
}
