package com.capslock.im.commons.packet.inbound;

import lombok.Data;

/**
 * Created by capslock1874.
 */
@Data
public abstract class AbstractSocketPacket {
    private final long senderUid;
}
