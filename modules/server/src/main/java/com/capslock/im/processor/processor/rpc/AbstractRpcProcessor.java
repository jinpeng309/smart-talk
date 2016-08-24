package com.capslock.im.processor.processor.rpc;

import com.capslock.im.model.Session;
import com.capslock.im.model.event.Event;
import com.capslock.im.model.event.rpcEvent.RpcEvent;
import com.capslock.im.model.event.rpcEvent.RpcEventType;

import java.util.ArrayList;

/**
 * Created by capslock1874.
 */
public abstract class AbstractRpcProcessor {
    abstract public void process(final RpcEvent event, final Session session, final ArrayList<Event> output);

    abstract public RpcEventType getType();
}
