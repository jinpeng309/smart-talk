package com.capslock.im.commons.packet.inbound;

import lombok.Data;

/**
 * Created by capslock1874.
 */
@Data
public abstract class AbstractSocketInboundPacket {
    private final long senderUid;
}
