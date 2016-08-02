package com.capslock.im.component;

import lombok.Data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

/**
 * Created by capslock1874.
 */
@Data
public class Session {
    private final long uid;
    private final HashMap<String, ClientInfo> clientsMap = new HashMap<>();

    public Session(final long uid, final ClientInfo clientInfo) {
        this.uid = uid;
        addClient(clientInfo);
    }

    public Session(final long uid, final Collection<ClientInfo> clients) {
        this.uid = uid;
        addClients(clients);
    }

    public Optional<ClientInfo> getClient(final String devUuid) {
        return Optional.ofNullable(clientsMap.get(devUuid));
    }

    public Collection<ClientInfo> getAllClients() {
        return clientsMap.values();
    }

    public void addClient(final ClientInfo client) {
        clientsMap.put(client.getDeviceUuid(), client);
    }

    public void addClients(final Collection<ClientInfo> clients) {
        clients.forEach(this::addClient);
    }
}
