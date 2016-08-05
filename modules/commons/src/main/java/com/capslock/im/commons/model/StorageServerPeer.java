package com.capslock.im.commons.model;

import com.capslock.im.commons.definition.ServerPeerJsonDefinition;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created by capslock1874.
 */
@Data
public class StorageServerPeer extends Peer implements Comparable<StorageServerPeer> {
    @JsonProperty(value = ServerPeerJsonDefinition.SERVER_IP)
    protected final String serverIp;

    @Override
    public PeerType getType() {
        return PeerType.STORAGE_SERVER;
    }

    @Override
    public int compareTo(@SuppressWarnings("NullableProblems") final StorageServerPeer other) {
        if (other == null) {
            return 1;
        } else {
            return getServerIp().compareTo(other.getServerIp());
        }
    }
}
