package com.capslock.im.commons.packet.rpc;

import com.capslock.im.commons.model.ClientPeer;
import com.capslock.im.commons.packet.inbound.PrivateChatMessagePacket;
import com.capslock.im.commons.packet.protocol.rpc.StorePrivateChatMessageRpcProtocol;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created by capslock1874.
 */
@Data
public final class StorePrivateChatMessageRpcRequest {
    @JsonProperty(StorePrivateChatMessageRpcProtocol.Request.OWNER)
    private final ClientPeer owner;
    @JsonProperty(StorePrivateChatMessageRpcProtocol.Request.DATA)
    private final PrivateChatMessagePacket messagePacket;

}
