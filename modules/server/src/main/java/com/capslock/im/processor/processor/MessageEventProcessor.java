package com.capslock.im.processor.processor;

import com.capslock.im.commons.annotations.Protocol;
import com.capslock.im.commons.model.ClientPeer;
import com.capslock.im.commons.packet.AbstractSocketPacket;
import com.capslock.im.commons.packet.cluster.ClientToSessionClusterPacket;
import com.capslock.im.commons.packet.cluster.ClusterPacket;
import com.capslock.im.commons.packet.cluster.PacketType;
import com.capslock.im.commons.packet.inbound.PrivateChatMessagePacket;
import com.capslock.im.commons.packet.protocol.PrivateChatMessageProtocol;
import com.capslock.im.event.ClusterPacketInboundEvent.ClusterPacketInboundEvent;
import com.capslock.im.event.ClusterPacketOutboundEvent.ClusterPacketOutboundEvent;
import com.capslock.im.event.ClusterPacketOutboundEvent.SessionToClientPacketRequest;
import com.capslock.im.event.ClusterPacketOutboundEvent.SessionToSessionPacketRequest;
import com.capslock.im.event.Event;
import com.capslock.im.event.EventType;
import com.capslock.im.event.rpcEvent.StorePrivateChatMessageRequestEvent;
import com.capslock.im.model.Session;

import java.util.ArrayList;

/**
 * Created by capslock1874.
 */
@Protocol(PrivateChatMessageProtocol.NAME)
public class MessageEventProcessor implements PacketEventProcessor {

    @Override
    public void process(final Event event, final Session session, final ArrayList<Event> output) {
        if (event.getType() == EventType.CLUSTER_PACKET_INBOUND) {
            final ClusterPacket clusterClusterPacket = ((ClusterPacketInboundEvent) event).getClusterPacket();

            if (clusterClusterPacket.getType() == PacketType.C2S) {
                final ClientToSessionClusterPacket clientToSessionPacket = (ClientToSessionClusterPacket) clusterClusterPacket;
                final ClientPeer from = (ClientPeer) clientToSessionPacket.getFrom();
                final PrivateChatMessagePacket messagePacket = (PrivateChatMessagePacket) clusterClusterPacket.getPacket();
                final long receiverUid = messagePacket.getTo();
                final SessionToSessionPacketRequest request = new SessionToSessionPacketRequest(
                        clusterClusterPacket.getPacket(), from, receiverUid);
                output.add(new ClusterPacketOutboundEvent(request));
                output.add(new StorePrivateChatMessageRequestEvent(from, messagePacket));
            } else if (clusterClusterPacket.getType() == PacketType.S2S) {
                final AbstractSocketPacket packet = clusterClusterPacket.getPacket();
                session.getAllClients().forEach(clientPeer -> output.add(new ClusterPacketOutboundEvent(
                        new SessionToClientPacketRequest(packet, clientPeer))));
            }
        }
    }
}
