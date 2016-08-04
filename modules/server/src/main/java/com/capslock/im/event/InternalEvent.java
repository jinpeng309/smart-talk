package com.capslock.im.event;

/**
 * Created by capslock1874.
 */
public abstract class InternalEvent extends Event {
    @Override
    public EventType getType() {
        return EventType.INTERNAL;
    }
}
