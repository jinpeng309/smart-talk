package com.capslock.im.cluster;

import com.capslock.im.cluster.event.ServerNodeAddEvent;
import com.capslock.im.cluster.event.ServerNodeRemovedEvent;
import com.capslock.im.commons.model.LogicServerPeer;
import com.capslock.im.commons.model.ServerPeer;
import com.capslock.im.component.ComponentIfc;
import com.google.common.eventbus.EventBus;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.nodes.PersistentEphemeralNode;
import org.springframework.beans.factory.annotation.Autowired;
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
public abstract class AbstractServerClusterManager implements ComponentIfc {
    private static final String logicServerPath = "/talk/logic";

    private CopyOnWriteArrayList<ServerPeer> serverPeers = new CopyOnWriteArrayList<>();

    @Autowired
    private CuratorFramework curatorClient;

    private TreeCache serverNodeTreeCache;

    @PostConstruct
    @Override
    public void setup() throws Exception {
        serverNodeTreeCache = new TreeCache(curatorClient, getServerPath());
        serverNodeTreeCache.start();
        serverPeers.addAll(getInitServerList());
        addServerClusterListener();
    }

    public boolean contains(final ServerPeer serverPeer) {
        return serverPeers.contains(serverPeer);
    }

    public List<ServerPeer> getServerList() {
        return serverPeers;
    }

    private List<? extends ServerPeer> getInitServerList() {
        Map<String, ChildData> serverDataMap = serverNodeTreeCache.getCurrentChildren(getServerPath());
        while (serverDataMap == null) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            serverDataMap = serverNodeTreeCache.getCurrentChildren(getServerPath());
        }
        return serverDataMap.values().stream().map(childData ->
                new LogicServerPeer(new String(childData.getData()))).sorted().collect(Collectors.toList());
    }

    public void registerServer(final ServerPeer serverPeer) throws Exception {
        PersistentEphemeralNode node = new PersistentEphemeralNode(curatorClient,
                PersistentEphemeralNode.Mode.EPHEMERAL, getServerPath(serverPeer),
                serverPeer.getServerIp().getBytes());
        node.start();
        node.waitForInitialCreate(3, TimeUnit.SECONDS);
    }

    private String getServerPath(final ServerPeer serverPeer) {
        return getServerPath() + "/" + serverPeer.getServerIp();
    }

    private void addServerClusterListener() {
        serverNodeTreeCache.getListenable().addListener((client, event) -> {
            switch (event.getType()) {
                case NODE_ADDED:
                    processServerNodeAdded(event);
                    break;
                case NODE_REMOVED:
                    processServerNodeRemoved(event);
                    break;
                case NODE_UPDATED:
                    break;
            }
        });
    }

    private void processServerNodeRemoved(final TreeCacheEvent event) {
        if (!getServerPath().equals(event.getData().getPath())) {
            final String serverIp = event.getData().getPath().substring(getServerPath().length() + 1);
            final LogicServerPeer logicServerPeer = new LogicServerPeer(serverIp);
            serverPeers.remove(logicServerPeer);
            Collections.sort(serverPeers);
            getEventBus().post(new ServerNodeRemovedEvent(logicServerPeer));
        }
    }

    private void processServerNodeAdded(final TreeCacheEvent event) {
        if (!getServerPath().equals(event.getData().getPath())) {
            final String serverIp = event.getData().getPath().substring(getServerPath().length() + 1);
            final LogicServerPeer logicServerPeer = new LogicServerPeer(serverIp);
            if (!serverPeers.contains(logicServerPeer)) {
                serverPeers.add(logicServerPeer);
                Collections.sort(serverPeers);
                getEventBus().post(new ServerNodeAddEvent(logicServerPeer));
            }
        }
    }

    abstract public EventBus getEventBus();

    abstract public String getServerPath();

    public int serverSize() {
        return serverPeers.size();
    }
}
