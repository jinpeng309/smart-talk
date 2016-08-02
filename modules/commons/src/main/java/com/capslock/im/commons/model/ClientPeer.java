package com.capslock.im.commons.model;

import com.capslock.im.commons.definition.ClientPeerJsonDefinition;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created by capslock1874.
 */
@Data
public class ClientPeer extends Peer {
    @JsonProperty(value = ClientPeerJsonDefinition.CLIENT_IP)
    private final String clientIp;
    @JsonProperty(value = ClientPeerJsonDefinition.DEVICE_UUID)
    private final String deviceUuid;
    @JsonProperty(value = ClientPeerJsonDefinition.USER_ID)
    private final long uid;
    @JsonProperty(value = ClientPeerJsonDefinition.SERVER_IP)
    private final String connServerNodeIp;

    @Override
    public PeerType getType() {
        return PeerType.CLIENT;
    }
}
