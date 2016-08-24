package com.capslock.im.component.cache;

import com.capslock.im.commons.model.ClientPeer;
import com.google.common.collect.ImmutableSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

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

    public Observable<ImmutableSet<ClientPeer>> getClientsAsync(final long uid) {
        return Observable.create(new Observable.OnSubscribe<ImmutableSet<ClientPeer>>() {
            @Override
            public void call(final Subscriber<? super ImmutableSet<ClientPeer>> subscriber) {
                subscriber.onNext(getClients(uid));
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io());
    }

    public Observable<Void> addClientAsync(final long uid, final String connServerIp, final String devUuid,
            final String clientIp) {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(final Subscriber<? super Void> subscriber) {
                addClient(uid, connServerIp, devUuid, clientIp);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io());
    }

    public void addClient(final long uid, final String connServerIp, final String devUuid, final String clientIp) {
        redisTemplate.boundHashOps(String.valueOf(uid)).put(connServerIp, devUuid + "_" + clientIp);
    }

    public void removeClient(final long uid, final String connServerIp) {
        redisTemplate.boundHashOps(String.valueOf(uid)).delete(connServerIp);
    }

    public Observable<Void> removeClientAsync(final long uid, final String connServerIp) {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(final Subscriber<? super Void> subscriber) {
                removeClient(uid, connServerIp);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io());
    }
}
