package com.capslock.im.component;

import com.capslock.im.cluster.ClusterManager;
import com.capslock.im.cluster.LogicServerNodeSelector;
import com.capslock.im.commons.annotations.Protocol;
import com.capslock.im.commons.model.ClientPeer;
import com.capslock.im.commons.model.LogicServerPeer;
import com.capslock.im.commons.packet.cluster.Packet;
import com.capslock.im.commons.packet.cluster.PacketType;
import com.capslock.im.commons.packet.cluster.SessionToSessionPacket;
import com.capslock.im.commons.util.NetUtils;
import com.capslock.im.config.LogicServerCondition;
import com.capslock.im.event.LogicServerNodeAddEvent;
import com.capslock.im.model.AbstractClusterPacketRequest;
import com.capslock.im.model.SessionToSessionPacketRequest;
import com.capslock.im.plugin.filter.PacketFilter;
import com.capslock.im.plugin.postProcessor.PacketPostProcessor;
import com.capslock.im.plugin.processor.PacketProcessor;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import lombok.Data;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TransferQueue;

/**
 * Created by capslock1874.
 */
@Component
@Conditional(LogicServerCondition.class)
public class SessionManager extends MessageReceiver<Packet> {
    @Autowired
    private ConnectedClientsCache connectedClientsCache;

    @Autowired
    private LogicServerNodeSelector logicServerNodeSelector;

    @Autowired
    private ClusterManager clusterManager;

    @Autowired
    @Qualifier("logicServerClusterEventBus")
    private EventBus logicServerCusterEventBus;

    @Autowired
    @Qualifier("connServerClusterEventBus")
    private EventBus connServerClusterEventBus;

    @Autowired
    private SessionMessageQueueManager sessionMessageQueueManager;

    private final ConcurrentHashMap<Long, Session> sessionMap = new ConcurrentHashMap<>(10000);

    private ImmutableMap<String, List<PacketProcessor>> processorMap;
    private ImmutableMap<String, List<PacketFilter>> packetFilterMap;
    private ImmutableMap<String, List<PacketPostProcessor>> packetPostPacketMap;

    private final ArrayList<TransferQueue<ProcessorItem>> processorItemQueue = new ArrayList<>();
    private final ArrayList<QueueListener> processorItemListener = new ArrayList<>();

    private String localHost;
    private LogicServerPeer localServerPeer;

    @Override
    public String getComponentName() {
        return "sm";
    }

    @PostConstruct
    @Override
    public void setup() throws Exception {
        super.setup();
        logicServerCusterEventBus.register(this);
        connServerClusterEventBus.register(this);
        localHost = NetUtils.getLocalHost().intern();
        localServerPeer = new LogicServerPeer(localHost);
        clusterManager.registerLogicServer(localServerPeer);
        initProcessorMap();
        initPacketFilter();
        initPacketPostProcessor();
        initProcessorItemQueue();
    }

    private void initProcessorItemQueue() {
        final int availableProcessor = Runtime.getRuntime().availableProcessors();
        if (processorItemQueue.isEmpty()) {
            for (int i = 0; i < availableProcessor; i++) {
                final TransferQueue<ProcessorItem> queue = new LinkedTransferQueue<>();
                processorItemQueue.add(queue);
                final QueueListener queueListener = new QueueListener(queue);
                processorItemListener.add(queueListener);
                queueListener.setName(getComponentName() + "-inbound-thread-" + i);
                queueListener.start();
            }
        }
    }

    private void initPacketPostProcessor() {
        packetPostPacketMap = ImmutableMap.of();
    }

    private void initPacketFilter() {
        packetFilterMap = ImmutableMap.of();
    }

    private void initProcessorMap() {
        final Reflections reflections = new Reflections("com.capslock.im.plugin.processor");
        final HashMap<String, List<PacketProcessor>> processors = new HashMap<>();
        reflections.getTypesAnnotatedWith(Protocol.class)
                .forEach(clazz -> {
                    try {
                        final String protocolName = clazz.getAnnotation(Protocol.class).value();
                        if (processors.containsKey(protocolName)) {
                            processors.get(protocolName).add((PacketProcessor) clazz.newInstance());
                        } else {
                            processors.put(protocolName, Lists.newArrayList((PacketProcessor) clazz.newInstance()));
                        }
                    } catch (InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });
        processorMap = ImmutableMap.copyOf(processors);
    }

    public void start() {

    }

    @Override
    public void processInboundMessage(final Packet packet) {
        final ClientPeer client = (ClientPeer) packet.getFrom();
        final Session session = getOrCreateSession(client.getUid());
        postProcessorItem(createProcessorItem(session, packet));
    }

    private ProcessorItem createProcessorItem(final Session session, final Packet packet) {
        final String protocolName = packet.getProtocolPacket().getName();
        return new ProcessorItem(packet, session, getPacketFilterList(protocolName),
                getPacketProcessorList(protocolName), getPacketPostProcessor(protocolName));
    }

    private void postProcessorItem(final ProcessorItem processorItem) {
        final int index = processorItem.getPacket().getDispatchIndex();
        processorItemQueue.get(Math.abs(index) % processorItemQueue.size()).add(processorItem);
    }

    private List<PacketFilter> getPacketFilterList(final String protocol) {
        List<PacketFilter> result = packetFilterMap.get(protocol);
        if (result == null) {
            result = Collections.emptyList();
        }
        return result;
    }

    private List<PacketProcessor> getPacketProcessorList(final String protocol) {
        List<PacketProcessor> result = processorMap.get(protocol);
        if (result == null) {
            result = Collections.emptyList();
        }
        return result;
    }

    private List<PacketPostProcessor> getPacketPostProcessor(final String protocol) {
        List<PacketPostProcessor> result = packetPostPacketMap.get(protocol);
        if (result == null) {
            result = Collections.emptyList();
        }
        return result;
    }

    private void postOutputMessage(final Packet packet) {
        sessionMessageQueueManager.postMessage(packet);
    }

    private void processOutputPacketRequest(final Collection<AbstractClusterPacketRequest> packets) {
        packets.forEach(request -> {
            if (request.getType() == PacketType.S2S) {
                final SessionToSessionPacketRequest packetRequest =
                        (SessionToSessionPacketRequest) request;
                final long receiverUid = packetRequest.getReceiverUid();
                final Session localSession = sessionMap.get(receiverUid);
                if (localSession != null) {
                    final SessionToSessionPacket outPacket = new SessionToSessionPacket(localServerPeer, localServerPeer,
                            packetRequest.getPacket(), packetRequest.getSenderClient(), receiverUid);
                    postMessage(outPacket);
                } else {
                    final LogicServerPeer toLogicServer = logicServerNodeSelector.selectByUid(receiverUid);
                    final SessionToSessionPacket outPacket = new SessionToSessionPacket(localServerPeer, toLogicServer,
                            packetRequest.getPacket(), packetRequest.getSenderClient(), receiverUid);
                    postOutputMessage(outPacket);
                }
            }
        });
    }

    public Session getOrCreateSession(final long uid) {
        return Optional.ofNullable(sessionMap.get(uid)).orElseGet(() -> createSession(uid));
    }

    public Session createSession(final long uid) {
        final ImmutableSet<ClientPeer> clients = connectedClientsCache.getClients(uid);
        final Session session = new Session(uid, clients);
        sessionMap.put(uid, session);
        return session;
    }

    public void cleanUselessSession() {
        sessionMap.forEachKey(4, uid -> {
            if (!Objects.equals(logicServerNodeSelector.selectByUid(uid).getServerIp(), localHost)) {
                sessionMap.remove(uid);
            }
        });
    }

    @Subscribe
    public void handleLogicServerNodeAdded(final LogicServerNodeAddEvent event) {
        cleanUselessSession();
    }

    @Data
    private static final class ProcessorItem {
        private final Packet packet;
        private final Session session;
        private final List<PacketFilter> packetFilterList;
        private final List<PacketProcessor> processorList;
        private final List<PacketPostProcessor> postProcessorList;
    }

    private final class QueueListener extends Thread {
        private final TransferQueue<ProcessorItem> queue;
        private volatile boolean stop = false;

        public QueueListener(final TransferQueue<ProcessorItem> queue) {
            this.queue = queue;
        }

        public void shutdown() {
            stop = true;
        }

        @Override
        public void run() {
            while (!stop) {
                try {
                    final ProcessorItem item = queue.take();
                    final Session session = item.getSession();
                    final Packet packet = item.getPacket();
                    boolean needStop = false;
                    final ArrayList<AbstractClusterPacketRequest> output = new ArrayList<>();
                    final List<PacketFilter> filterList = item.getPacketFilterList();
                    for (int i = 0; i < filterList.size() && !needStop; i++) {
                        needStop = filterList.get(i).process(packet, session, output);
                    }
                    if (!needStop) {
                        item.getProcessorList().forEach(processor -> processor.process(packet, session, output));
                        item.getPostProcessorList().forEach(processor -> processor.process(packet, session, output));
                    }
                    processOutputPacketRequest(output);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
