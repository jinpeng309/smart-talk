package com.capslock.im.component;

import com.capslock.im.commons.packet.AbstractMessageWithDispatchIndex;
import com.google.common.base.Preconditions;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TransferQueue;

/**
 * Created by capslock1874.
 */
public abstract class MessageReceiver<T extends AbstractMessageWithDispatchIndex> implements ComponentIfc {
    private final ArrayList<BlockingQueue<T>> inboundQueues = new ArrayList<>();
    private final ArrayList<QueueListener> inboundListeners = new ArrayList<>();

    abstract public String getName();

    abstract public SchedulerType getSchedulerType();

    public enum SchedulerType {
        IO, COMPUTATION
    }

    @PostConstruct
    @Override
    public void setup() throws Exception {
        Preconditions.checkState(inboundQueues.isEmpty());
        final int availableProcessor = Runtime.getRuntime().availableProcessors();
        if (getSchedulerType() == SchedulerType.COMPUTATION) {
            initComputationScheduler(availableProcessor);
        } else if (getSchedulerType() == SchedulerType.IO) {
            initIOScheduler(availableProcessor);
        }
    }

    private void initComputationScheduler(final int availableProcessor) {
        final int queueSize = availableProcessor % 2 == 0 ? availableProcessor + 1 : availableProcessor;
        for (int i = 0; i < queueSize; i++) {
            final TransferQueue<T> queue = new LinkedTransferQueue<>();
            inboundQueues.add(queue);
            final QueueListener queueListener = new QueueListener(queue);
            inboundListeners.add(queueListener);
            queueListener.setName(getName() + "-inbound-thread-" + i);
            queueListener.start();
        }
    }

    private void initIOScheduler(final int availableProcessor) {
        for (int i = 0; i < availableProcessor * 4 + 1; i++) {
            final BlockingQueue<T> queue = new ArrayBlockingQueue<>(10000);
            inboundQueues.add(queue);
            final QueueListener queueListener = new QueueListener(queue);
            inboundListeners.add(queueListener);
            queueListener.setName(getName() + "-inbound-thread-" + i);
            queueListener.start();
        }
    }

    public void stop() {
        inboundListeners.forEach(QueueListener::shutdown);
    }

    public boolean postMessage(final T message) {
        final int index = message.getDispatchIndex();
        return inboundQueues.get(Math.abs(index) % inboundQueues.size()).offer(message);
    }

    abstract public void processInboundMessage(final T message);

    private final class QueueListener extends Thread {
        private final BlockingQueue<T> queue;
        private volatile boolean stop = false;

        public QueueListener(final BlockingQueue<T> queue) {
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
