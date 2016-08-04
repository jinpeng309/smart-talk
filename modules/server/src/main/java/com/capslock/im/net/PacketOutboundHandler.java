package com.capslock.im.net;

import com.capslock.im.commons.packet.AbstractSocketPacket;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by capslock1874.
 */
public class PacketOutboundHandler extends MessageToByteEncoder<AbstractSocketPacket> {
    private static final ObjectMapper mapper = new ObjectMapper().registerModule(new Jdk8Module());

    @Override
    protected void encode(final ChannelHandlerContext channelHandlerContext, final AbstractSocketPacket packet,
            final ByteBuf byteBuf) throws Exception {
        byteBuf.writeBytes(mapper.writeValueAsBytes(packet));
    }

}
