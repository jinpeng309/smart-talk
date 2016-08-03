package com.capslock.im.commons.packet.cluster;

import com.capslock.im.commons.model.ClientPeer;
import com.capslock.im.commons.model.LogicServerPeer;
import com.capslock.im.commons.packet.ProtocolPacket;

/**
 * Created by capslock1874.
 */
public class SessionToClientPacket extends Packet {

    public SessionToClientPacket(final LogicServerPeer from, final ClientPeer to, final ProtocolPacket protocolPacket) {
        super(from, to, protocolPacket);
    }

    @Override
    public int getDispatchIndex() {
        return ((ClientPeer) getTo()).getDeviceUuid().hashCode();
    }

    @Override
    public PacketType getType() {
        return PacketType.S2C;
    }

}
