package com.capslock.im.model.event.rpcEvent;

import com.capslock.im.model.event.Event;
import com.capslock.im.model.event.EventType;

/**
 * Created by capslock1874.
 */
public abstract class RpcEvent extends Event {

    public abstract long getOwnerUid();

    public abstract RpcEventType getInternalEventType();
    @Override
    public EventType getType() {
        return EventType.RPC;
    }
}
