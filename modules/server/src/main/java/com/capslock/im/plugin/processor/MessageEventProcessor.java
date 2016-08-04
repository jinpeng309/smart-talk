package com.capslock.im.plugin.processor;

import com.capslock.im.commons.annotations.Protocol;
import com.capslock.im.commons.deserializer.ProtocolPacketDeserializer;
import com.capslock.im.commons.model.ClientPeer;
import com.capslock.im.commons.packet.ProtocolPacket;
import com.capslock.im.commons.packet.cluster.ClientToSessionPacket;
import com.capslock.im.commons.packet.cluster.Packet;
import com.capslock.im.commons.packet.cluster.PacketType;
import com.capslock.im.commons.packet.inbound.PrivateChatMessagePacket;
import com.capslock.im.commons.packet.protocol.PrivateChatMessageProtocol;
import com.capslock.im.component.Session;
import com.capslock.im.event.ClusterPacketInboundEvent.ClusterPacketInboundEvent;
import com.capslock.im.event.ClusterPacketOutboundEvent.ClusterPacketOutboundEvent;
import com.capslock.im.event.ClusterPacketOutboundEvent.SessionToClientPacketRequest;
import com.capslock.im.event.ClusterPacketOutboundEvent.SessionToSessionPacketRequest;
import com.capslock.im.event.Event;
import com.capslock.im.event.EventType;
import com.capslock.im.event.InternalEvent.StorePrivateChatMessageRequestEvent;

import java.util.ArrayList;

/**
 * Created by capslock1874.
 */
@Protocol(PrivateChatMessageProtocol.NAME)
public class MessageEventProcessor implements PacketEventProcessor {

    @Override
    public void process(final Event event, final Session session, final ArrayList<Event> output) {
        if (event.getType() == EventType.CLUSTER_PACKET_INBOUND) {
            final Packet packet = ((ClusterPacketInboundEvent) event).getPacket();

            if (packet.getType() == PacketType.C2S) {
                final ClientToSessionPacket clientToSessionPacket = (ClientToSessionPacket) packet;
                final ClientPeer from = (ClientPeer) clientToSessionPacket.getFrom();
                final PrivateChatMessagePacket messagePacket = (PrivateChatMessagePacket) ProtocolPacketDeserializer
                        .deserialize(packet.getProtocolPacket())
                        .orElseThrow(() -> new IllegalArgumentException("illegal packet " + packet.getProtocolPacket()));
                final long receiverUid = messagePacket.getTo();
                final SessionToSessionPacketRequest request = new SessionToSessionPacketRequest(packet.getProtocolPacket(),
                        from, receiverUid);
                output.add(new ClusterPacketOutboundEvent(request));
                output.add(new StorePrivateChatMessageRequestEvent(from, messagePacket));
            } else if (packet.getType() == PacketType.S2S) {
                final ProtocolPacket protocolPacket = packet.getProtocolPacket();
                session.getAllClients().forEach(clientPeer -> output.add(new ClusterPacketOutboundEvent(
                        new SessionToClientPacketRequest(protocolPacket, clientPeer))));
            }
        }
    }
}
