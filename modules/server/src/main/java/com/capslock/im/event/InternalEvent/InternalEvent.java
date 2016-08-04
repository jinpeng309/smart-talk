package com.capslock.im.event.InternalEvent;

import com.capslock.im.event.Event;
import com.capslock.im.event.EventType;

/**
 * Created by capslock1874.
 */
public abstract class InternalEvent extends Event {
    @Override
    public EventType getType() {
        return EventType.INTERNAL;
    }
}
