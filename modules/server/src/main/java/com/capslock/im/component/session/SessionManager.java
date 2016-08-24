package com.capslock.im.component.session;

import com.capslock.im.cluster.ClusterManager;
import com.capslock.im.cluster.LogicServerNodeSelector;
import com.capslock.im.cluster.event.LogicServerNodeAddEvent;
import com.capslock.im.commons.annotations.Protocol;
import com.capslock.im.commons.model.ClientPeer;
import com.capslock.im.commons.model.LogicServerPeer;
import com.capslock.im.commons.packet.cluster.ClusterPacket;
import com.capslock.im.commons.packet.cluster.SessionToClientClusterPacket;
import com.capslock.im.commons.packet.cluster.SessionToSessionClusterPacket;
import com.capslock.im.commons.packet.cluster.SessionToStorageClusterPacket;
import com.capslock.im.commons.packet.rpc.StorePrivateChatMessageRpcRequest;
import com.capslock.im.commons.util.NetUtils;
import com.capslock.im.component.ConnectedClientsCache;
import com.capslock.im.component.MessageReceiver;
import com.capslock.im.config.LogicServerCondition;
import com.capslock.im.event.ClusterPacketInboundEvent.ClusterPacketInboundEvent;
import com.capslock.im.event.ClusterPacketOutboundEvent.AbstractClusterPacketRequest;
import com.capslock.im.event.ClusterPacketOutboundEvent.ClusterPacketOutboundEvent;
import com.capslock.im.event.ClusterPacketOutboundEvent.SessionToClientPacketRequest;
import com.capslock.im.event.ClusterPacketOutboundEvent.SessionToSessionPacketRequest;
import com.capslock.im.event.Event;
import com.capslock.im.event.EventType;
import com.capslock.im.event.rpcEvent.RpcEvent;
import com.capslock.im.event.rpcEvent.StorePrivateChatMessageRequestEvent;
import com.capslock.im.model.Session;
import com.capslock.im.processor.filter.EventFilter;
import com.capslock.im.processor.postProcessor.EventPostProcessor;
import com.capslock.im.processor.processor.PacketEventProcessor;
import com.capslock.im.processor.processor.rpc.RpcEventProcessor;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
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
public class SessionManager extends MessageReceiver<Event> {
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

    private ImmutableMap<String, List<PacketEventProcessor>> processorMap;
    private ImmutableMap<String, List<EventFilter>> packetFilterMap;
    private ImmutableMap<String, List<EventPostProcessor>> packetPostPacketMap;
    private RpcEventProcessor rpcEventProcessor = new RpcEventProcessor();
    private final ArrayList<TransferQueue<ProcessItem>> processorItemQueue = new ArrayList<>();
    private final ArrayList<QueueListener> processorItemListener = new ArrayList<>();

    private String localHost;
    private LogicServerPeer localServerPeer;

    @Override
    public String getName() {
        return "sm";
    }

    @Override
    public SchedulerType getSchedulerType() {
        return SchedulerType.COMPUTATION;
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
                final TransferQueue<ProcessItem> queue = new LinkedTransferQueue<>();
                processorItemQueue.add(queue);
                final QueueListener queueListener = new QueueListener(queue);
                processorItemListener.add(queueListener);
                queueListener.setName(getName() + "-inbound-thread-" + i);
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
        final HashMap<String, List<PacketEventProcessor>> processors = new HashMap<>();
        reflections.getTypesAnnotatedWith(Protocol.class)
                .forEach(clazz -> {
                    try {
                        final String protocolName = clazz.getAnnotation(Protocol.class).value();
                        if (processors.containsKey(protocolName)) {
                            processors.get(protocolName).add((PacketEventProcessor) clazz.newInstance());
                        } else {
                            processors.put(protocolName, Lists.newArrayList((PacketEventProcessor) clazz.newInstance()));
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
    public void processInboundMessage(final Event event) {
        if (event.getType() == EventType.CLUSTER_PACKET_INBOUND) {
            final ClusterPacket clusterPacket = ((ClusterPacketInboundEvent) event).getClusterPacket();
            final ClientPeer client = (ClientPeer) clusterPacket.getFrom();
            final Session session = getOrCreateSession(client.getUid());
            postProcessorItem(createClusterPacketInboundProcessItem(session, clusterPacket));
        } else if (event.getType() == EventType.RPC) {
            final RpcEvent rpcEvent = (RpcEvent) event;
            final Session session = getOrCreateSession(rpcEvent.getOwnerUid());
            postProcessorItem(createRpcEventProcessItem(session, event));
        }
    }

    private ProcessItem createClusterPacketInboundProcessItem(final Session session, final ClusterPacket clusterPacket) {
        final String protocolName = clusterPacket.getProtocolName();
        return new ProcessItem(new ClusterPacketInboundEvent(clusterPacket), session, getPacketFilterList(protocolName),
                getPacketProcessorList(protocolName), getPacketPostProcessor(protocolName));
    }

    private ProcessItem createRpcEventProcessItem(final Session session, final Event event) {
        return new ProcessItem(event, session);
    }

    private void postProcessorItem(final ProcessItem processItem) {
        final int index = processItem.getEvent().getDispatchIndex();
        processorItemQueue.get(Math.abs(index) % processorItemQueue.size()).add(processItem);
    }

    private List<EventFilter> getPacketFilterList(final String protocol) {
        List<EventFilter> result = packetFilterMap.get(protocol);
        if (result == null) {
            result = Collections.emptyList();
        }
        return result;
    }

    private List<PacketEventProcessor> getPacketProcessorList(final String protocol) {
        return processorMap.getOrDefault(protocol, Collections.emptyList());
    }

    private List<EventPostProcessor> getPacketPostProcessor(final String protocol) {
        return packetPostPacketMap.getOrDefault(protocol, Collections.emptyList());
    }

    private void postOutputMessage(final ClusterPacket clusterPacket) {
        sessionMessageQueueManager.postMessage(clusterPacket);
    }

    private void processOutputClusterPacketEvent(final ClusterPacketOutboundEvent event) {
        switch (event.getRequestType()) {
            case S2S:
                processSessionToSessionPacketRequest((SessionToSessionPacketRequest) event.getRequest());
                break;
            case S2C:
                processSessionToClientPacketRequest(event.getRequest());
                break;
        }
    }

    private void processSessionToClientPacketRequest(final AbstractClusterPacketRequest request) {
        final ClientPeer to = ((SessionToClientPacketRequest) request).getTo();
        final SessionToClientClusterPacket packet = new SessionToClientClusterPacket(localServerPeer, to, request.getPacket());
        postOutputMessage(packet);
    }

    private void processSessionToSessionPacketRequest(final SessionToSessionPacketRequest request) {
        final long receiverUid = request.getReceiverUid();
        final LogicServerPeer toLogicServer = logicServerNodeSelector.selectByUid(receiverUid);
        final Session localSession = sessionMap.get(receiverUid);
        if (localSession != null && localServerPeer.equals(toLogicServer)) {
            final SessionToSessionClusterPacket outPacket = new SessionToSessionClusterPacket(localServerPeer, localServerPeer,
                    request.getPacket(), request.getSenderClient(), receiverUid);
            postMessage(new ClusterPacketInboundEvent(outPacket));
        } else {
            final SessionToSessionClusterPacket outPacket = new SessionToSessionClusterPacket(localServerPeer, toLogicServer,
                    request.getPacket(), request.getSenderClient(), receiverUid);
            postOutputMessage(outPacket);
        }
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

    private void processOutputEvent(final ArrayList<Event> output) {
        output.forEach(event -> {
            switch (event.getType()) {
                case CLUSTER_PACKET_OUTBOUND:
                    processOutputClusterPacketEvent((ClusterPacketOutboundEvent) event);
                    break;
                case RPC:
                    processOutputRpcEvent((RpcEvent) event);
                    break;
            }
        });
    }

    private void processOutputRpcEvent(final RpcEvent event) {
        switch (event.getInternalEventType()) {
            case STORE_PRIVATE_CHAT_MESSAGE_REQUEST:
                processStorePrivateChatMessageRequest((StorePrivateChatMessageRequestEvent) event);
                break;
        }
    }

    private void processStorePrivateChatMessageRequest(final StorePrivateChatMessageRequestEvent event) {
        final StorePrivateChatMessageRpcRequest request = new StorePrivateChatMessageRpcRequest(event.getOwner(),
                event.getPacket());
        sessionMessageQueueManager.postMessage(new SessionToStorageClusterPacket(localServerPeer, null,
                request.getProtocolName(), request, event.getOwner()));
    }

    public void stop() {
        processorItemListener.forEach(QueueListener::shutdown);
    }

    @Subscribe
    public void handleLogicServerNodeAdded(final LogicServerNodeAddEvent event) {
        cleanUselessSession();
    }

    @Data
    @AllArgsConstructor
    private static final class ProcessItem {
        private final Event event;
        private final Session session;
        private List<EventFilter> eventFilterList;
        private List<PacketEventProcessor> processorList;
        private List<EventPostProcessor> postProcessorList;

        public ProcessItem(final Event event, final Session session) {
            this.event = event;
            this.session = session;
        }
    }

    private final class QueueListener extends Thread {
        private final TransferQueue<ProcessItem> queue;
        private volatile boolean stop = false;

        public QueueListener(final TransferQueue<ProcessItem> queue) {
            this.queue = queue;
        }

        public void shutdown() {
            stop = true;
        }

        @Override
        public void run() {
            while (!stop) {
                try {
                    final ProcessItem item = queue.take();
                    final ArrayList<Event> output = new ArrayList<>();
                    final Event event = item.getEvent();
                    final Session session = item.getSession();
                    if (event.getType() == EventType.CLUSTER_PACKET_INBOUND) {
                        boolean needStop = false;
                        final List<EventFilter> filterList = item.getEventFilterList();
                        for (int i = 0; i < filterList.size() && !needStop; i++) {
                            needStop = filterList.get(i).process(event, session, output);
                        }
                        if (!needStop) {
                            item.getProcessorList().forEach(processor -> processor.process(event, session, output));
                            item.getPostProcessorList().forEach(processor -> processor.process(event, session, output));
                        }
                    } else if (event.getType() == EventType.RPC) {
                        rpcEventProcessor.process((RpcEvent) event, session, output);
                    }

                    processOutputEvent(output);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
