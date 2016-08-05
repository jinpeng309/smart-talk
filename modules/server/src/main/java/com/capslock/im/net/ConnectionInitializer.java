package com.capslock.im.net;

import com.capslock.im.component.connection.ConnectionManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * Created by capslock1874.
 */
public class ConnectionInitializer extends ChannelInitializer {
    private final ConnectionManager connectionManager;

    public ConnectionInitializer(final ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    protected void initChannel(final Channel channel) throws Exception {
        final ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast(new PacketOutboundHandler());
        pipeline.addLast(new JsonObjectDecoder());
        pipeline.addLast(new StringDecoder());
        pipeline.addLast(new PacketInboundHandler(connectionManager));
        pipeline.addLast(new IdleStateHandler(300, 300, 300));
    }
}
