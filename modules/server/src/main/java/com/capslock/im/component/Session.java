package com.capslock.im.component;

import com.capslock.im.commons.model.ClientPeer;
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
    private final HashMap<String, ClientPeer> clientsMap = new HashMap<>();

    public Session(final long uid, final ClientPeer clientInfo) {
        this.uid = uid;
        addClient(clientInfo);
    }

    public Session(final long uid, final Collection<ClientPeer> clients) {
        this.uid = uid;
        addClients(clients);
    }

    public Optional<ClientPeer> getClient(final String devUuid) {
        return Optional.ofNullable(clientsMap.get(devUuid));
    }

    public Collection<ClientPeer> getAllClients() {
        return clientsMap.values();
    }

    public void addClient(final ClientPeer client) {
        clientsMap.put(client.getDeviceUuid(), client);
    }

    public void addClients(final Collection<ClientPeer> clients) {
        clients.forEach(this::addClient);
    }
}
