package com.capslock.im.plugin.filter;

import com.capslock.im.commons.packet.cluster.Packet;
import com.capslock.im.component.Session;

import java.util.ArrayList;

/**
 * Created by capslock1874.
 */
public interface PacketFilter {
    boolean process(final Packet packet, final Session session, final ArrayList<Packet> output);
}
