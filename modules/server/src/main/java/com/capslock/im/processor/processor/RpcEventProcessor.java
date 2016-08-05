package com.capslock.im.processor.processor;

import com.capslock.im.component.session.Session;
import com.capslock.im.event.Event;
import com.capslock.im.event.rpcEvent.RpcEvent;
import com.capslock.im.event.rpcEvent.StorePrivateChatMessageSuccessEvent;

import java.util.ArrayList;

/**
 * Created by capslock1874.
 */
public class RpcEventProcessor {
    public void process(final RpcEvent event, final Session session, final ArrayList<Event> output) {
        switch (event.getInternalEventType()) {
            case STORE_PRIVATE_CHAT_MESSAGE_SUCCEED:
                final StorePrivateChatMessageSuccessEvent successEvent = (StorePrivateChatMessageSuccessEvent) event;
                break;
        }
    }
}
