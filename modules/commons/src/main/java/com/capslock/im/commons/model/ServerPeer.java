package com.capslock.im.commons.model;

import com.capslock.im.commons.definition.ServerPeerJsonDefinition;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by capslock1874.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class ServerPeer<T extends ServerPeer> extends Peer implements Comparable<T> {
    @JsonProperty(value = ServerPeerJsonDefinition.SERVER_IP)
    protected String serverIp;

    public ServerPeer() {
    }

    public ServerPeer(final String ip) {
        this.serverIp = ip;
    }

    @Override
    public int compareTo(@SuppressWarnings("NullableProblems") final T other) {
        if (other == null) {
            return 1;
        } else {
            return getServerIp().compareTo(other.getServerIp());
        }
    }
}