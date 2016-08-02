package com.capslock.im.commons.packet.request;

import com.capslock.im.commons.annotations.Protocol;
import com.capslock.im.commons.packet.protocol.AuthenticationProtocol;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * Created by capslock1874.
 */
@Protocol(AuthenticationProtocol.NAME)
@Getter
public class SocketInboundAuthPacket extends AbstractSocketInboundPacket {
    private String token;
    private int deviceType;
    private String deviceUuid;

    public SocketInboundAuthPacket(@JsonProperty(value = AuthenticationProtocol.Inbound.FROM, required = true) final long senderUid,
            @JsonProperty(value = AuthenticationProtocol.Inbound.TOKEN, required = true) final String token,
            @JsonProperty(value = AuthenticationProtocol.Inbound.DEVICE_TYPE, required = true) final int deviceType,
            @JsonProperty(value = AuthenticationProtocol.Inbound.DEVICE_UUID, required = true) final String deviceUuid) {
        super(senderUid);
        this.token = token;
        this.deviceType = deviceType;
        this.deviceUuid = deviceUuid;
    }
}
