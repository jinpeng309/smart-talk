package com.capslock.im.event.InternalEvent;

import com.capslock.im.commons.model.ClientPeer;
import lombok.Data;

/**
 * Created by capslock1874.
 */
@Data
public class StorePrivateChatMessageSuccessEvent extends InternalEvent {
    private final ClientPeer clientPeer;

    @Override
    public int getDispatchIndex() {
        return (int) clientPeer.getUid();
    }
}
