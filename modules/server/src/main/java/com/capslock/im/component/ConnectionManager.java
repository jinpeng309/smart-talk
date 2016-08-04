package com.capslock.im.component;

import com.capslock.im.cluster.ClusterManager;
import com.capslock.im.cluster.LogicServerNodeSelector;
import com.capslock.im.commons.model.ClientPeer;
import com.capslock.im.commons.model.ConnServerPeer;
import com.capslock.im.commons.model.LogicServerPeer;
import com.capslock.im.commons.packet.ProtocolPacket;
import com.capslock.im.commons.packet.cluster.ClientToSessionPacket;
import com.capslock.im.commons.packet.cluster.Packet;
import com.capslock.im.commons.packet.cluster.PacketType;
import com.capslock.im.commons.packet.inbound.request.SocketInboundAuthPacket;
import com.capslock.im.commons.packet.outbound.response.AbstractSocketOutboundPacket;
import com.capslock.im.commons.packet.outbound.response.SocketOutboundAuth;
import com.capslock.im.commons.serializer.PacketSerializer;
import com.capslock.im.commons.util.NetUtils;
import com.capslock.im.config.ConnServerCondition;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.eventbus.EventBus;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.net.UnknownHostException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by capslock1874.
 */
@Component
@Conditional(ConnServerCondition.class)
public class ConnectionManager extends MessageReceiver<Packet> {
    private final ConcurrentHashMap<String, Connection> connectionMap = new ConcurrentHashMap<>();
    private String localHost;
    private ConnServerPeer localServerPeer;
    private long uid = 1L;

    @Autowired
    private LogicServerNodeSelector logicServerNodeSelector;

    @Autowired
    private ConnectionMessageQueueManager connectionMessageQueueManager;

    @Autowired
    private ConnectedClientsCache connectedClientsCache;

    @Autowired
    private ClusterManager clusterManager;

    @Autowired
    @Qualifier("logicServerClusterEventBus")
    private EventBus logicServerCusterEventBus;

    @Autowired
    @Qualifier("connServerClusterEventBus")
    private EventBus connServerClusterEventBus;

    @Override
    public String getName() {
        return "c2s";
    }

    @Override
    public void setup() throws Exception {
        super.setup();
        logicServerCusterEventBus.register(this);
        connServerClusterEventBus.register(this);
        try {
            localHost = NetUtils.getLocalHost();
        } catch (UnknownHostException e) {
            throw new Exception(e);
        }
        localServerPeer = new ConnServerPeer(localHost);
        clusterManager.registerConnServer(localServerPeer);
    }

    @Override
    public void processInboundMessage(final Packet packet) {
        if (packet.getType() == PacketType.S2C) {
            processPacketFromLogicServer(packet);
        }
    }

    private void processPacketFromLogicServer(final Packet packet) {
        final ClientPeer client = (ClientPeer) packet.getTo();
        getConnection(client.getDeviceUuid()).ifPresent(conn -> writeRawPacket(conn, packet.getProtocolPacket()));
    }

    public void processPacketFromClient(final String deviceUuid, final ProtocolPacket protocolPacket) {
        final Connection connection = connectionMap.get(deviceUuid);
        if (connection != null) {
            final ClientPeer clientPeer = connection.getClientPeer();
            final LogicServerPeer logicServerPeer = logicServerNodeSelector.selectByUid(clientPeer.getUid());
            final ClientToSessionPacket packet = new ClientToSessionPacket(clientPeer, logicServerPeer, protocolPacket);
            connectionMessageQueueManager.postMessage(packet);
        }
    }

    public boolean authClient(final String connId, final ChannelHandlerContext ctx,
            final SocketInboundAuthPacket authPacket) {
        final long uid = this.uid++;
        final ClientPeer clientPeer = new ClientPeer(connId, authPacket.getDeviceUuid(), uid, localHost);
        final Connection connection = new Connection(clientPeer, ctx);
        addConnection(connection);
        final SocketOutboundAuth packet = new SocketOutboundAuth(0);
        writePacket(connection, packet);
        return true;
    }

    public <T extends AbstractSocketOutboundPacket> void writePacket(final Connection connection, final T packet) {
        try {
            connection.write(PacketSerializer.serialize(packet));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void writeRawPacket(final Connection connection, final ProtocolPacket packet){
        connection.write(packet);
    }

    private void addConnection(final Connection connection) {
        final ClientPeer clientPeer = connection.getClientPeer();
        connectionMap.put(clientPeer.getDeviceUuid(), connection);
        connectedClientsCache.addClient(clientPeer.getUid(), clientPeer.getConnServerNodeIp(),
                clientPeer.getDeviceUuid(), clientPeer.getClientIp());
    }

    private Optional<Connection> getConnection(final String deviceUuid) {
        return Optional.ofNullable(connectionMap.get(deviceUuid));
    }

    public void clientClose(final String deviceUuid) {
        getConnection(deviceUuid).ifPresent(connection -> {
            final ClientPeer client = connection.getClientPeer();
            connectedClientsCache.removeClient(client.getUid(), client.getConnServerNodeIp());
            connectionMap.remove(deviceUuid);
        });
    }

    @Override
    public SchedulerType getSchedulerType() {
        return SchedulerType.COMPUTATION;
    }
}
