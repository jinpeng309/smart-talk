package com.capslock.im.commons.packet;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by capslock1874.
 */
@Data
@AllArgsConstructor
public final class ProtocolPacket {
    private String name;
    private JsonNode data;
}
