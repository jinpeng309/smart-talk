package com.capslock.im.service;

import com.capslock.im.component.SessionManager;
import com.capslock.im.event.InternalEvent.StorePrivateChatMessageRequestEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by capslock1874.
 */
@Service
public class MessageService {
    @Autowired
    private SessionManager sessionManager;

    public void processStorePrivateChatMessageEvent(final StorePrivateChatMessageRequestEvent event) {
    }
}
