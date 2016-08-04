package com.capslock.im.commons.packet.cluster;

import com.capslock.im.commons.model.AdminPeer;
import com.capslock.im.commons.model.ClientPeer;
import com.capslock.im.commons.packet.AbstractSocketPacket;

/**
 * Created by capslock1874.
 */
public class AdminCommandClusterPacket extends ClusterPacket {
    public AdminCommandClusterPacket(final AdminPeer from, final ClientPeer to, final AbstractSocketPacket packet) {
        super(from, to, packet.getProtocolName(), packet);
    }

    @Override
    public int getDispatchIndex() {
        return (int) ((ClientPeer) getTo()).getUid();
    }

    @Override
    public PacketType getType() {
        return PacketType.ADMIN_CMD;
    }
}
