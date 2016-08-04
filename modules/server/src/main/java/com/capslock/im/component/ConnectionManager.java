package com.capslock.im.component;

import com.capslock.im.cluster.ClusterManager;
import com.capslock.im.cluster.LogicServerNodeSelector;
import com.capslock.im.commons.model.ClientPeer;
import com.capslock.im.commons.model.ConnServerPeer;
import com.capslock.im.commons.model.LogicServerPeer;
import com.capslock.im.commons.packet.AbstractSocketPacket;
import com.capslock.im.commons.packet.cluster.ClientToSessionClusterPacket;
import com.capslock.im.commons.packet.cluster.ClusterPacket;
import com.capslock.im.commons.packet.cluster.PacketType;
import com.capslock.im.commons.packet.inbound.SocketAuthRequestPacket;
import com.capslock.im.commons.packet.outbound.SocketAuthResponse;
import com.capslock.im.commons.util.NetUtils;
import com.capslock.im.config.ConnServerCondition;
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
public class ConnectionManager extends MessageReceiver<ClusterPacket> {
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
    public void processInboundMessage(final ClusterPacket clusterPacket) {
        if (clusterPacket.getType() == PacketType.S2C) {
            processPacketFromLogicServer(clusterPacket);
        }
    }

    private void processPacketFromLogicServer(final ClusterPacket clusterPacket) {
        final ClientPeer client = (ClientPeer) clusterPacket.getTo();
        getConnection(client.getDeviceUuid()).ifPresent(conn -> writeRawPacket(conn, clusterPacket.getPacket()));
    }

    public void processPacketFromClient(final String deviceUuid, final AbstractSocketPacket socketPacket) {
        final Connection connection = connectionMap.get(deviceUuid);
        if (connection != null) {
            final ClientPeer clientPeer = connection.getClientPeer();
            final LogicServerPeer logicServerPeer = logicServerNodeSelector.selectByUid(clientPeer.getUid());
            final ClientToSessionClusterPacket packet = new ClientToSessionClusterPacket(clientPeer, logicServerPeer,
                    socketPacket);
            connectionMessageQueueManager.postMessage(packet);
        }
    }

    public boolean authClient(final String connId, final ChannelHandlerContext ctx,
            final SocketAuthRequestPacket authPacket) {
        final long uid = this.uid++;
        final ClientPeer clientPeer = new ClientPeer(connId, authPacket.getDeviceUuid(), uid, localHost);
        final Connection connection = new Connection(clientPeer, ctx);
        addConnection(connection);
        final SocketAuthResponse packet = new SocketAuthResponse(0);
        writePacket(connection, packet);
        return true;
    }

    public <T extends AbstractSocketPacket> void writePacket(final Connection connection, final T packet) {
        connection.write(packet);
    }

    public void writeRawPacket(final Connection connection, final AbstractSocketPacket packet) {
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
