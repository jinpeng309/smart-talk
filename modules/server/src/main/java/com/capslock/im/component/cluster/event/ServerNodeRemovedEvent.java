package com.capslock.im.component.cluster.event;

import com.capslock.im.commons.model.ServerPeer;
import lombok.Data;

/**
 * Created by capslock1874.
 */
@Data
public class ServerNodeRemovedEvent {
    private final ServerPeer serverPeer;
}
