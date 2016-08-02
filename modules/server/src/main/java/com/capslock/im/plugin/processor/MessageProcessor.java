package com.capslock.im.plugin.processor;

import com.capslock.im.commons.annotations.Protocol;
import com.capslock.im.commons.model.ClientPeer;
import com.capslock.im.commons.model.LogicServerPeer;
import com.capslock.im.commons.packet.cluster.ClientToSessionPacket;
import com.capslock.im.commons.packet.cluster.Packet;
import com.capslock.im.commons.packet.cluster.SessionToClientPacket;
import com.capslock.im.commons.packet.protocol.PrivateChatMessageProtocol;
import com.capslock.im.component.Session;

import java.util.ArrayList;

/**
 * Created by capslock1874.
 */
@Protocol(PrivateChatMessageProtocol.NAME)
public class MessageProcessor implements PacketProcessor {

    @Override
    public void process(final Packet packet, final Session session, final ArrayList<Packet> output) {
        final ClientToSessionPacket clientToSessionPacket = (ClientToSessionPacket) packet;
        final ClientPeer from = (ClientPeer) clientToSessionPacket.getFrom();
        final LogicServerPeer to = (LogicServerPeer) clientToSessionPacket.getTo();
        final SessionToClientPacket sessionToClientPacket = new SessionToClientPacket(to, from, packet.getProtocolPacket());
        output.add(sessionToClientPacket);
    }
}
