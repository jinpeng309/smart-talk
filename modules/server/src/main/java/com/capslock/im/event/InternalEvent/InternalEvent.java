package com.capslock.im.event.InternalEvent;

import com.capslock.im.event.Event;
import com.capslock.im.event.EventType;

/**
 * Created by capslock1874.
 */
public abstract class InternalEvent extends Event {

    public abstract long getOwnerUid();

    public abstract InternalEventType getInternalEventType();
    @Override
    public EventType getType() {
        return EventType.INTERNAL;
    }
}
