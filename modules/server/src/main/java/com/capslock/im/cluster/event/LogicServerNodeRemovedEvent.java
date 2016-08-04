package com.capslock.im.cluster.event;

import com.capslock.im.commons.model.LogicServerPeer;
import lombok.Data;

/**
 * Created by capslock1874.
 */
@Data
public class LogicServerNodeRemovedEvent {
    private final LogicServerPeer logicServerPeer;
}
