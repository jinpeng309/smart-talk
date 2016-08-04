package com.capslock.im.commons.packet.cluster;

import com.capslock.im.commons.model.ClientPeer;
import com.capslock.im.commons.model.LogicServerPeer;
import com.capslock.im.commons.packet.AbstractSocketPacket;

/**
 * Created by capslock1874.
 */
public class ClientToSessionClusterPacket extends ClusterPacket {

    public ClientToSessionClusterPacket(final ClientPeer from, final LogicServerPeer to, final AbstractSocketPacket packet) {
        super(from, to, packet.getProtocolName(), packet);
    }

    @Override
    public int getDispatchIndex() {
        return (int) ((ClientPeer) getFrom()).getUid();
    }

    @Override
    public PacketType getType() {
        return PacketType.C2S;
    }
}
