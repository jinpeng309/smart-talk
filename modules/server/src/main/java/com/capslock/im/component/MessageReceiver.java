package com.capslock.im.component;

import com.capslock.im.commons.packet.AbstractMessageWithDispatchIndex;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TransferQueue;

/**
 * Created by capslock1874.
 */
@Component
public abstract class MessageReceiver<T extends AbstractMessageWithDispatchIndex> implements ComponentIfc {
    private final ArrayList<TransferQueue<T>> inboundQueues = new ArrayList<>();
    private final ArrayList<QueueListener> inboundListeners = new ArrayList<>();

    abstract public String getComponentName();

    @PostConstruct
    @Override
    public void setup() throws Exception {
        final int availableProcessor = Runtime.getRuntime().availableProcessors();
        if (inboundQueues.isEmpty()) {
            for (int i = 0; i < availableProcessor; i++) {
                final TransferQueue<T> queue = new LinkedTransferQueue<>();
                inboundQueues.add(queue);
                final QueueListener queueListener = new QueueListener(queue);
                inboundListeners.add(queueListener);
                queueListener.setName(getComponentName() + "-inbound-thread-" + i);
                queueListener.start();
            }
        }
    }

    public void stop() {
        inboundListeners.forEach(QueueListener::shutdown);
    }

    public void postMessage(final T message) {
        final int index = message.getDispatchIndex();
        inboundQueues.get(Math.abs(index) % inboundQueues.size()).add(message);
    }

    public void postMessages(final Collection<T> messages) {
        messages.forEach(message -> {
            final int index = message.getDispatchIndex();
            inboundQueues.get(Math.abs(index) % inboundQueues.size()).add(message);
        });
    }

    abstract public void processInboundMessage(final T message);

    private final class QueueListener extends Thread {
        private final TransferQueue<T> queue;
        private volatile boolean stop = false;

        public QueueListener(final TransferQueue<T> queue) {
            this.queue = queue;
        }

        public void shutdown() {
            stop = true;
        }

        @Override
        public void run() {
            while (!stop) {
                try {
                    processInboundMessage(queue.take());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
