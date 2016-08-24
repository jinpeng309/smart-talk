package com.capslock.im.component.cluster;

import com.capslock.im.commons.model.ServerPeer;
import com.capslock.im.component.ComponentIfc;
import com.google.common.hash.Hashing;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Created by capslock1874.
 */
@Component
public abstract class ServerNodeSelector implements ComponentIfc {

    @PostConstruct
    @Override
    public void setup() throws Exception {

    }

    public ServerPeer selectByUid(final long uid) {
        return getNodeList().get(Hashing.consistentHash(uid, getNodeList().size()));
    }

    public abstract List<ServerPeer> getNodeList();
}
