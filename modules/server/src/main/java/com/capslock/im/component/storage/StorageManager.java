package com.capslock.im.component.storage;

import com.capslock.im.component.MessageReceiver;
import com.capslock.im.config.StorageServerCondition;
import com.capslock.im.event.Event;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

/**
 * Created by capslock1874.
 */
@Component
@Conditional(StorageServerCondition.class)
public class StorageManager extends MessageReceiver<Event> {

    public void start() {

    }

    @Override
    public String getName() {
        return "storage-manager";
    }

    @Override
    public SchedulerType getSchedulerType() {
        return SchedulerType.COMPUTATION;
    }

    @Override
    public void processInboundMessage(final Event message) {

    }
}
