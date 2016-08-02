package com.capslock.im.component;

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

    public ImmutableSet<ClientInfo> getClients(final long uid) {
        final Map<Object, Object> entries = redisTemplate.boundHashOps(String.valueOf(uid)).entries();
        final ImmutableSet.Builder<ClientInfo> builder = ImmutableSet.builder();
        entries.forEach((connServerIp, devUuid) -> builder.add(
                new ClientInfo(uid, devUuid.toString(), connServerIp.toString())));
        return builder.build();
    }

    public void addClient(final long uid, final String connServerIp, final String devUuid) {
        redisTemplate.boundHashOps(String.valueOf(uid)).put(connServerIp, devUuid);
    }

    public void removeClient(final long uid, final String connServerIp) {
        redisTemplate.boundHashOps(String.valueOf(uid)).delete(connServerIp);
    }
}
