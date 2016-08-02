package com.capslock.im.commons.packet.inbound;

import com.capslock.im.commons.annotations.Protocol;
import com.capslock.im.commons.packet.protocol.PrivateChatMessageProtocol;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * Created by capslock1874.
 */
@Protocol(PrivateChatMessageProtocol.NAME)
@Getter
public final class PrivateChatMessagePacket extends AbstractSocketInboundPacket {
    private final long to;
    private final String content;

    public PrivateChatMessagePacket(@JsonProperty(value = PrivateChatMessageProtocol.FROM, required = true) final long from,
            @JsonProperty(value = PrivateChatMessageProtocol.TO, required = true) final long to,
            @JsonProperty(value = PrivateChatMessageProtocol.CONTENT, required = true) final String content) {
        super(from);
        this.to = to;
        this.content = content;
    }
}
