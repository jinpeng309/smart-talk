package com.capslock.im.processor.filter;

import com.capslock.im.component.Session;
import com.capslock.im.event.Event;

import java.util.ArrayList;

/**
 * Created by capslock1874.
 */
public interface EventFilter {
    boolean process(final Event event, final Session session, final ArrayList<Event> output);
}
