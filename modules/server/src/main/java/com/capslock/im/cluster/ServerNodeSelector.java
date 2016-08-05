package com.capslock.im.cluster;

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
public abstract class ServerNodeSelector<T extends ServerPeer> implements ComponentIfc {

    @PostConstruct
    @Override
    public void setup() throws Exception {

    }

    public T selectByUid(final long uid) {
        return getNodeList().get(Hashing.consistentHash(uid, getNodeList().size()));
    }

    public abstract List<T> getNodeList();
}
