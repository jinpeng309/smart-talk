package com.capslock.im.model.event;

import com.capslock.im.commons.packet.AbstractMessageWithDispatchIndex;

/**
 * Created by capslock1874.
 */
public abstract class Event extends AbstractMessageWithDispatchIndex {
    public abstract EventType getType();
}
