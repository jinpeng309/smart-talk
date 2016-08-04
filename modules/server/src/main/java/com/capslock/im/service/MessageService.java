package com.capslock.im.service;

import com.capslock.im.component.MessageReceiver;
import com.capslock.im.component.SessionManager;
import com.capslock.im.event.Event;
import com.capslock.im.event.InternalEvent.StorePrivateChatMessageRequestEvent;
import com.capslock.im.event.InternalEvent.StorePrivateChatMessageSuccessEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by capslock1874.
 */
@Service
public class MessageService extends MessageReceiver<Event> {
    @Autowired
    private SessionManager sessionManager;

    public void processStorePrivateChatMessageEvent(final StorePrivateChatMessageRequestEvent event) {
        sessionManager.postMessage(new StorePrivateChatMessageSuccessEvent(event.getOwner()));
    }

    @Override
    public String getName() {
        return "MessageService";
    }

    @Override
    public SchedulerType getSchedulerType() {
        return SchedulerType.IO;
    }

    @Override
    public void processInboundMessage(final Event message) {

    }
}
