package com.capslock.im.component;

import com.capslock.im.commons.model.ClientPeer;
import com.capslock.im.commons.packet.ProtocolPacket;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;

/**
 * Created by capslock1874.
 */
@Data
public class Connection {
    private final ClientPeer clientPeer;
    private final ChannelHandlerContext ctx;

    public void write(final ProtocolPacket packet){
        ctx.writeAndFlush(packet);
    }
}
