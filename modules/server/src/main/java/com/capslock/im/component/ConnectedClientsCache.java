package com.capslock.im.component;

import com.capslock.im.commons.model.ClientPeer;
import com.google.common.collect.ImmutableSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by capslock1874.
 */
@Component
public class ConnectedClientsCache {
    @Autowired
    private StringRedisTemplate redisTemplate;

    public ImmutableSet<ClientPeer> getClients(final long uid) {
        final Map<Object, Object> entries = redisTemplate.boundHashOps(String.valueOf(uid)).entries();
        final ImmutableSet.Builder<ClientPeer> builder = ImmutableSet.builder();
        entries.forEach((connServerIp, clientInfo) -> {
            final String strClientInfo = clientInfo.toString();
            final String deviceUuid = strClientInfo.substring(0, strClientInfo.lastIndexOf("_"));
            final String clientIp = strClientInfo.substring(strClientInfo.lastIndexOf("_"));
            builder.add(new ClientPeer(clientIp, deviceUuid, uid, connServerIp.toString()));
        });
        return builder.build();
    }

    public void addClient(final long uid, final String connServerIp, final String devUuid, final String clientIp) {
        redisTemplate.boundHashOps(String.valueOf(uid)).put(connServerIp, devUuid + "_" + clientIp);
    }

    public void removeClient(final long uid, final String connServerIp) {
        redisTemplate.boundHashOps(String.valueOf(uid)).delete(connServerIp);
    }
}
