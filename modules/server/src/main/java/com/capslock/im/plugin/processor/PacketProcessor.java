package com.capslock.im.plugin.processor;

import com.capslock.im.commons.packet.cluster.Packet;
import com.capslock.im.component.Session;
import com.capslock.im.model.AbstractClusterPacketRequest;

import java.util.ArrayList;

/**
 * Created by capslock1874.
 */
public interface PacketProcessor {
    void process(final Packet packet, final Session session, final ArrayList<AbstractClusterPacketRequest> output);
}
