package com.capslock.im.model.event.rpcEvent;

import com.capslock.im.commons.model.ClientPeer;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by capslock1874.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class StorePrivateChatMessageSuccessEvent extends RpcEvent {
    private final String uuid;
    private final ClientPeer owner;

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
        return RpcEventType.STORE_PRIVATE_CHAT_MESSAGE_SUCCEED;
    }
}
