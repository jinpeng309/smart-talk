package com.capslock.im.processor.processor;

import com.capslock.im.model.Session;
import com.capslock.im.model.event.Event;

import java.util.ArrayList;

/**
 * Created by capslock1874.
 */
public interface PacketEventProcessor {
    void process(final Event event, final Session session, final ArrayList<Event> output);
}
