package com.capslock.im.commons.packet.cluster;

import com.capslock.im.commons.model.AdminPeer;
import com.capslock.im.commons.model.ClientPeer;
import com.capslock.im.commons.packet.ProtocolPacket;

/**
 * Created by capslock1874.
 */
public class AdminCommandPacket extends Packet {
    public AdminCommandPacket(final AdminPeer from, final ClientPeer to, final ProtocolPacket protocolPacket) {
        super(from, to, protocolPacket);
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
