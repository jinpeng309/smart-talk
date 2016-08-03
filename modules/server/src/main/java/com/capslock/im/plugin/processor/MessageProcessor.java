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
import com.capslock.im.model.AbstractClusterPacketRequest;
import com.capslock.im.model.SessionToClientPacketRequest;
import com.capslock.im.model.SessionToSessionPacketRequest;

import java.util.ArrayList;

/**
 * Created by capslock1874.
 */
@Protocol(PrivateChatMessageProtocol.NAME)
public class MessageProcessor implements PacketProcessor {

    @Override
    public void process(final Packet packet, final Session session, final ArrayList<AbstractClusterPacketRequest> output) {
        if (packet.getType() == PacketType.C2S) {
            final ClientToSessionPacket clientToSessionPacket = (ClientToSessionPacket) packet;
            final ClientPeer from = (ClientPeer) clientToSessionPacket.getFrom();
            final PrivateChatMessagePacket messagePacket = (PrivateChatMessagePacket) ProtocolPacketDeserializer
                    .deserialize(packet.getProtocolPacket())
                    .orElseThrow(() -> new IllegalArgumentException("illegal packet " + packet.getProtocolPacket()));
            final long receiverUid = messagePacket.getTo();

            final SessionToSessionPacketRequest request = new SessionToSessionPacketRequest(packet.getProtocolPacket(),
                    from, receiverUid);
            output.add(request);
        } else if (packet.getType() == PacketType.S2S) {
            final ProtocolPacket protocolPacket = packet.getProtocolPacket();
            session.getAllClients().forEach(clientPeer -> output.add(new SessionToClientPacketRequest(protocolPacket,
                    clientPeer)));
        }
    }
}
