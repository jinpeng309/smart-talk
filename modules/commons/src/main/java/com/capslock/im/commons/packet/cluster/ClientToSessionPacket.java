package com.capslock.im.commons.packet.cluster;

import com.capslock.im.commons.model.ClientPeer;
import com.capslock.im.commons.model.LogicServerPeer;
import com.capslock.im.commons.packet.ProtocolPacket;

/**
 * Created by capslock1874.
 */
public class ClientToSessionPacket extends Packet {

    public ClientToSessionPacket(final ClientPeer from, final LogicServerPeer to, final ProtocolPacket protocolPacket) {
        super(from, to, protocolPacket);
    }

    @Override
    public int getDispatchIndex() {
        return ((ClientPeer) getFrom()).getDeviceUuid().hashCode();
    }

    @Override
    public PacketType getType() {
        return PacketType.C2S;
    }
}
