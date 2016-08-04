package com.capslock.im.processor.processor;

import com.capslock.im.component.Session;
import com.capslock.im.event.Event;
import com.capslock.im.event.InternalEvent.InternalEvent;
import com.capslock.im.event.InternalEvent.StorePrivateChatMessageSuccessEvent;

import java.util.ArrayList;

/**
 * Created by capslock1874.
 */
public class InternalEventProcessor {
    public void process(final InternalEvent event, final Session session, final ArrayList<Event> output) {
        switch (event.getInternalEventType()) {
            case STORE_PRIVATE_CHAT_MESSAGE_SUCCEED:
                final StorePrivateChatMessageSuccessEvent successEvent = (StorePrivateChatMessageSuccessEvent) event;
                break;
        }
    }
}
