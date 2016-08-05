package com.capslock.im.service;

import com.capslock.im.component.MessageReceiver;
import com.capslock.im.component.session.SessionManager;
import com.capslock.im.event.Event;
import com.capslock.im.event.rpcEvent.RpcEvent;
import com.capslock.im.event.rpcEvent.StorePrivateChatMessageRequestEvent;
import com.capslock.im.event.rpcEvent.StorePrivateChatMessageSuccessEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by capslock1874.
 */
@Service
public class MessageService extends MessageReceiver<Event> {
    @Autowired
    private SessionManager sessionManager;

    private void processStorePrivateChatMessageEvent(final StorePrivateChatMessageRequestEvent event) {
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
    public void processInboundMessage(final Event event) {
        final RpcEvent rpcEvent = (RpcEvent) event;
        switch (((RpcEvent) event).getInternalEventType()) {
            case STORE_PRIVATE_CHAT_MESSAGE_REQUEST:
                processStorePrivateChatMessageEvent((StorePrivateChatMessageRequestEvent) rpcEvent);
                break;
        }
    }
}
