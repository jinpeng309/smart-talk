package com.capslock.im.plugin.postProcessor;

import com.capslock.im.commons.packet.cluster.Packet;
import com.capslock.im.component.Session;

import java.util.ArrayList;

/**
 * Created by capslock1874.
 */
public interface PacketPostProcessor {
    void process(final Packet packet, final Session session, final ArrayList<Packet> output);
}
