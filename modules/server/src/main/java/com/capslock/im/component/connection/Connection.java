package com.capslock.im.component.connection;

import com.capslock.im.commons.model.ClientPeer;
import com.capslock.im.commons.packet.AbstractSocketPacket;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;

/**
 * Created by capslock1874.
 */
@Data
public class Connection {
    private final ClientPeer clientPeer;
    private final ChannelHandlerContext ctx;

    public void write(final AbstractSocketPacket packet) {
        ctx.writeAndFlush(packet);
    }
}
