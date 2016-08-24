package com.capslock.im.commons.packet.inbound;

import com.capslock.im.commons.annotations.Protocol;
import com.capslock.im.commons.packet.AbstractSocketPacket;
import com.capslock.im.commons.packet.protocol.PrivateChatMessageProtocol;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * Created by capslock1874.
 */
@Protocol(PrivateChatMessageProtocol.NAME)
@Getter
public final class PrivateChatMessagePacket extends AbstractSocketPacket {
    private final long from;
    private final long to;
    private final String content;
    private final String uuid;

    public PrivateChatMessagePacket(@JsonProperty(value = PrivateChatMessageProtocol.FROM, required = true) final long from,
            @JsonProperty(value = PrivateChatMessageProtocol.TO, required = true) final long to,
            @JsonProperty(value = PrivateChatMessageProtocol.CONTENT, required = true) final String content,
            @JsonProperty(value = PrivateChatMessageProtocol.UUID, required = true) final String uuid) {
        this.from = from;
        this.to = to;
        this.content = content;
        this.uuid = uuid;
    }

    @Override
    public String getProtocolName() {
        return PrivateChatMessageProtocol.NAME;
    }
}
