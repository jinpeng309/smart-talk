package com.capslock.im.event.InternalEvent;

import com.capslock.im.commons.model.ClientPeer;
import com.capslock.im.commons.packet.inbound.PrivateChatMessagePacket;
import lombok.Data;

/**
 * Created by capslock1874.
 */
@Data
public class StorePrivateChatMessageRequestEvent extends InternalEvent {
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
    public InternalEventType getInternalEventType() {
        return InternalEventType.STORE_PRIVATE_CHAT_MESSAGE_REQUEST;
    }
}
