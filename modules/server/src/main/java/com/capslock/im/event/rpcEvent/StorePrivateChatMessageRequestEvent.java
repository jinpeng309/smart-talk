package com.capslock.im.event.rpcEvent;

import com.capslock.im.commons.model.ClientPeer;
import com.capslock.im.commons.packet.inbound.PrivateChatMessagePacket;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by capslock1874.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class StorePrivateChatMessageRequestEvent extends RpcEvent {
    private final ClientPeer owner;
    private final PrivateChatMessagePacket packet;

    @Override
    public int getDispatchIndex() {
        return (int) owner.getUid();
    }

    @Override
    public long getOwnerUid() {
        return owner.getUid();
    }

    @Override
    public RpcEventType getInternalEventType() {
        return RpcEventType.STORE_PRIVATE_CHAT_MESSAGE_REQUEST;
    }
}
