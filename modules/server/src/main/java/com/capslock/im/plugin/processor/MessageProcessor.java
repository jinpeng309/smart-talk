package com.capslock.im.plugin.processor;

import com.capslock.im.commons.annotations.Protocol;
import com.capslock.im.commons.deserializer.ProtocolPacketDeserializer;
import com.capslock.im.commons.model.ClientPeer;
import com.capslock.im.commons.model.LogicServerPeer;
import com.capslock.im.commons.packet.cluster.ClientToSessionPacket;
import com.capslock.im.commons.packet.cluster.Packet;
import com.capslock.im.commons.packet.cluster.PacketType;
import com.capslock.im.commons.packet.inbound.PrivateChatMessagePacket;
import com.capslock.im.commons.packet.protocol.PrivateChatMessageProtocol;
import com.capslock.im.commons.util.NetUtils;
import com.capslock.im.component.Session;
import com.capslock.im.model.AbstractClusterPacketRequest;
import com.capslock.im.model.SessionToSessionPacketRequest;

import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Created by capslock1874.
 */
@Protocol(PrivateChatMessageProtocol.NAME)
public class MessageProcessor implements PacketProcessor {
    private String localHost;
    private LogicServerPeer localServerPeer;

    public void setup() throws UnknownHostException {
        localHost = NetUtils.getLocalHost().intern();
        localServerPeer = new LogicServerPeer(localHost);
    }

    @Override
    public void process(final Packet packet, final Session session, final ArrayList<AbstractClusterPacketRequest> output) {
        if (packet.getType() == PacketType.C2S) {
            final ClientToSessionPacket clientToSessionPacket = (ClientToSessionPacket) packet;
            final ClientPeer from = (ClientPeer) clientToSessionPacket.getFrom();
            final PrivateChatMessagePacket messagePacket = (PrivateChatMessagePacket) ProtocolPacketDeserializer
                    .deserialize(packet.getProtocolPacket())
                    .orElseThrow(() -> new IllegalArgumentException("illegal packet " + packet.getProtocolPacket()));
            final long receiverUid = messagePacket.getTo();

            final SessionToSessionPacketRequest request = new SessionToSessionPacketRequest(from, receiverUid,
                    packet.getProtocolPacket());
            output.add(request);
        } else if (packet.getType() == PacketType.S2S) {
            packet.getProtocolPacket();
            session.getAllClients().forEach(clientInfo -> {
            });
        }
    }
}
