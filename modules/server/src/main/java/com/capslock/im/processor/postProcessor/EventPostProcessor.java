package com.capslock.im.processor.postProcessor;

import com.capslock.im.component.Session;
import com.capslock.im.event.Event;

import java.util.ArrayList;

/**
 * Created by capslock1874.
 */
public interface EventPostProcessor {
    void process(final Event event, final Session session, final ArrayList<Event> output);
}
