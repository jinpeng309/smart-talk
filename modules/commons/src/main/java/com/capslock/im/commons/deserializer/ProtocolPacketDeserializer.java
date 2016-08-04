package com.capslock.im.commons.deserializer;

import com.capslock.im.commons.annotations.Protocol;
import com.capslock.im.commons.packet.AbstractSocketPacket;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.google.common.collect.ImmutableMap;
import org.reflections.Reflections;

import java.util.Optional;

/**
 * Created by capslock1874.
 */
public class ProtocolPacketDeserializer {
    private static final ImmutableMap<String, Class<? extends AbstractSocketPacket>> protocolMap;
    private static final ObjectMapper mapper = new ObjectMapper().registerModule(new Jdk8Module());

    static {
        final ImmutableMap.Builder<String, Class<? extends AbstractSocketPacket>> mapBuilder = ImmutableMap.builder();
        final Reflections reflections = new Reflections("com.capslock.im.commons.packet.inbound");
        reflections
                .getSubTypesOf(AbstractSocketPacket.class)
                .forEach(clazz -> mapBuilder.put(clazz.getAnnotation(Protocol.class).value(), clazz));
        protocolMap = mapBuilder.build();
    }

    private ProtocolPacketDeserializer() {
        //no instance
    }

    public static Optional<? extends AbstractSocketPacket> deserialize(final String name, String data) {
        try {
            return Optional.of(mapper.readValue(data, protocolMap.get(name)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public static Optional<? extends AbstractSocketPacket> deserialize(final String name, final JsonNode data) {
        return deserialize(name, data.toString());
    }

    public static boolean isLegalProtocol(final String name) {
        return protocolMap.containsKey(name);
    }
}


