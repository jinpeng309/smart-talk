package com.capslock.im.commons.packet.rpc;

import com.capslock.im.commons.model.ClientPeer;
import com.capslock.im.commons.packet.AbstractSocketPacket;
import com.capslock.im.commons.packet.inbound.PrivateChatMessagePacket;
import com.capslock.im.commons.packet.protocol.rpc.StorePrivateChatMessageRpcProtocol;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by capslock1874.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public final class StorePrivateChatMessageRpcRequest extends AbstractSocketPacket {
    @JsonProperty(StorePrivateChatMessageRpcProtocol.Request.OWNER)
    private final ClientPeer owner;
    @JsonProperty(StorePrivateChatMessageRpcProtocol.Request.DATA)
    private final PrivateChatMessagePacket messagePacket;

    @Override
    public String getProtocolName() {
        return StorePrivateChatMessageRpcProtocol.NAME;
    }
}
