package com.capslock.im.cluster;

import com.capslock.im.cluster.event.LogicServerNodeAddEvent;
import com.capslock.im.cluster.event.LogicServerNodeRemovedEvent;
import com.capslock.im.commons.model.ConnServerPeer;
import com.capslock.im.commons.model.LogicServerPeer;
import com.capslock.im.component.ComponentIfc;
import com.google.common.eventbus.EventBus;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.nodes.PersistentEphemeralNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by capslock1874.
 */

@Component
public class ClusterManager implements ComponentIfc {
    private static final String logicServerPath = "/talk/logic";
    private static final String connServerPath = "/talk/conn";

    private CopyOnWriteArrayList<LogicServerPeer> logicServerPeers = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<ConnServerPeer> connServerPeers = new CopyOnWriteArrayList<>();

    @Autowired
    @Qualifier("logicServerClusterEventBus")
    private EventBus logicServerCusterEventBus;

    @Autowired
    @Qualifier("connServerClusterEventBus")
    private EventBus connServerClusterEventBus;

    @Autowired
    private CuratorFramework curatorClient;

    private TreeCache logicServerNodeTreeCache;
    private TreeCache connServerNodeTreeCache;


    @PostConstruct
    @Override
    public void setup() throws Exception {
        logicServerNodeTreeCache = new TreeCache(curatorClient, logicServerPath);
        logicServerNodeTreeCache.start();
        logicServerPeers.addAll(getInitLogicServerList());
        addLogicServerClusterListener();

        connServerNodeTreeCache = new TreeCache(curatorClient, connServerPath);
        connServerNodeTreeCache.start();
        connServerPeers.addAll(getInitConnServerList());

    }

    public boolean contains(final LogicServerPeer logicServerPeer) {
        return logicServerPeers.contains(logicServerPeer);
    }

    public boolean contains(final ConnServerPeer connServerPeer) {
        return connServerPeers.contains(connServerPeer);
    }


    public List<ConnServerPeer> getConnServerList() {
        return connServerPeers;
    }

    public List<LogicServerPeer> getLogicServerList() {
        return logicServerPeers;
    }

    private List<ConnServerPeer> getInitConnServerList() {
        Map<String, ChildData> serverDataMap = connServerNodeTreeCache.getCurrentChildren(connServerPath);
        while (serverDataMap == null) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            serverDataMap = connServerNodeTreeCache.getCurrentChildren(connServerPath);
        }
        return serverDataMap.values().stream().map(childData ->
                new ConnServerPeer(new String(childData.getData()))).sorted().collect(Collectors.toList());
    }

    private List<LogicServerPeer> getInitLogicServerList() {
        Map<String, ChildData> serverDataMap = logicServerNodeTreeCache.getCurrentChildren(logicServerPath);
        while (serverDataMap == null) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            serverDataMap = logicServerNodeTreeCache.getCurrentChildren(logicServerPath);
        }
        return serverDataMap.values().stream().map(childData ->
                new LogicServerPeer(new String(childData.getData()))).sorted().collect(Collectors.toList());
    }

    public void registerConnServer(final ConnServerPeer connServerPeer) throws Exception {
        PersistentEphemeralNode node = new PersistentEphemeralNode(curatorClient,
                PersistentEphemeralNode.Mode.EPHEMERAL, getConnServerPath(connServerPeer),
                connServerPeer.getServerIp().getBytes());
        node.start();
        node.waitForInitialCreate(3, TimeUnit.SECONDS);
    }


    public void registerLogicServer(final LogicServerPeer logicServerPeer) throws Exception {
        PersistentEphemeralNode node = new PersistentEphemeralNode(curatorClient,
                PersistentEphemeralNode.Mode.EPHEMERAL, getLogicServerPath(logicServerPeer),
                logicServerPeer.getServerIp().getBytes());
        node.start();
        node.waitForInitialCreate(3, TimeUnit.SECONDS);
    }

    private String getLogicServerPath(final LogicServerPeer logicServerPeer) {
        return logicServerPath + "/" + logicServerPeer.getServerIp();
    }

    private String getConnServerPath(final ConnServerPeer connServerPeer) {
        return connServerPath + "/" + connServerPeer.getServerIp();
    }


    private void addLogicServerClusterListener() {
        logicServerNodeTreeCache.getListenable().addListener((client, event) -> {
            switch (event.getType()) {
                case NODE_ADDED:
                    processLogicServerNodeAdded(event);
                    break;
                case NODE_REMOVED:
                    processLogicServerNodeRemoved(event);
                    break;
                case NODE_UPDATED:
                    break;
            }
        });
    }

    private void processLogicServerNodeRemoved(final TreeCacheEvent event) {
        if (!logicServerPath.equals(event.getData().getPath())) {
            final String serverIp = event.getData().getPath().substring(logicServerPath.length() + 1);
            final LogicServerPeer logicServerPeer = new LogicServerPeer(serverIp);
            logicServerPeers.remove(logicServerPeer);
            Collections.sort(logicServerPeers);
            logicServerCusterEventBus.post(new LogicServerNodeRemovedEvent(logicServerPeer));
        }
    }

    private void processLogicServerNodeAdded(final TreeCacheEvent event) {
        if (!logicServerPath.equals(event.getData().getPath())) {
            final String serverIp = event.getData().getPath().substring(logicServerPath.length() + 1);
            final LogicServerPeer logicServerPeer = new LogicServerPeer(serverIp);
            if (!logicServerPeers.contains(logicServerPeer)) {
                logicServerPeers.add(logicServerPeer);
                Collections.sort(logicServerPeers);
                logicServerCusterEventBus.post(new LogicServerNodeAddEvent(logicServerPeer));
            }
        }
    }

    public int logicServerSize() {
        return logicServerPeers.size();
    }
}
