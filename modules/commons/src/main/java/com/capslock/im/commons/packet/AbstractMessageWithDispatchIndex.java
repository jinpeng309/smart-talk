package com.capslock.im.commons.packet;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by capslock1874.
 */
public abstract class AbstractMessageWithDispatchIndex {
    @JsonIgnore
    public abstract int getDispatchIndex();
}
