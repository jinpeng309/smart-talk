package com.capslock.im.processor.processor.rpc;

import com.capslock.im.event.Event;
import com.capslock.im.event.rpcEvent.RpcEvent;
import com.capslock.im.event.rpcEvent.RpcEventType;
import com.capslock.im.model.Session;
import com.google.common.collect.ImmutableMap;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by capslock1874.
 */
public class RpcEventProcessor {
    private final Map<RpcEventType, ? extends AbstractRpcProcessor> processorMap;

    {
        final Reflections reflections = new Reflections("com.capslock.im.processor.processor.rpc");
        final HashMap<RpcEventType, AbstractRpcProcessor> processors = new HashMap<>();
        reflections
                .getSubTypesOf(AbstractRpcProcessor.class)
                .forEach(clazz -> {
                    try {
                        final AbstractRpcProcessor processor = clazz.newInstance();
                        processors.put(processor.getType(), processor);
                    } catch (InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });
        processorMap = ImmutableMap.copyOf(processors);
    }

    public void process(final RpcEvent event, final Session session, final ArrayList<Event> output) {
        processorMap.get(event.getInternalEventType()).process(event, session, output);
    }
}
