package com.capslock.im.net;

import com.capslock.im.commons.deserializer.ProtocolPacketDeserializer;
import com.capslock.im.commons.packet.AbstractSocketPacket;
import com.capslock.im.commons.packet.inbound.SocketAuthRequestPacket;
import com.capslock.im.commons.packet.protocol.AuthenticationProtocol;
import com.capslock.im.component.ConnectionManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.google.common.base.Strings;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;

import java.net.InetSocketAddress;

/**
 * Created by capslock1874.
 */
public class PacketInboundHandler extends SimpleChannelInboundHandler<String> {
    private static final ObjectMapper mapper = new ObjectMapper().registerModule(new Jdk8Module());
    private final ConnectionManager connectionManager;
    private String connId;
    private String deviceUuid;
    private boolean hasAuthorized = false;

    public PacketInboundHandler(final ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public void userEventTriggered(final ChannelHandlerContext ctx, final Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        if (evt == IdleState.ALL_IDLE) {
            ctx.close();
        }
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final String msg) throws Exception {
        final JsonNode jsonNode = mapper.readTree(msg);
        final String protocolName = jsonNode.fields().next().getKey();
        final AbstractSocketPacket socketPacket = ProtocolPacketDeserializer
                .deserialize(protocolName, jsonNode.get(protocolName))
                .orElseThrow(() -> new IllegalArgumentException("Illegal packet"));
        if (!hasAuthorized && !AuthenticationProtocol.NAME.equalsIgnoreCase(protocolName)) {
            ctx.close();
        } else if (!hasAuthorized) {
            final SocketAuthRequestPacket authPacket = (SocketAuthRequestPacket) socketPacket;
            deviceUuid = authPacket.getDeviceUuid();
            hasAuthorized = connectionManager.authClient(connId, ctx, authPacket);
        } else {
            connectionManager.processPacketFromClient(deviceUuid, socketPacket);
        }
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        ctx.close();
    }

    @Override
    public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        if (!Strings.isNullOrEmpty(deviceUuid)) {
            connectionManager.clientClose(deviceUuid);
        }

    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        final InetSocketAddress clientAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        connId = clientAddress.getHostName() + ":" + clientAddress.getPort();
    }
}
