package com.capslock.im.commons.deserializer;

import com.capslock.im.commons.model.ClientPeer;
import com.capslock.im.commons.model.LogicServerPeer;
import com.capslock.im.commons.packet.AbstractSocketPacket;
import com.capslock.im.commons.packet.cluster.ClientToSessionClusterPacket;
import com.capslock.im.commons.packet.cluster.ClusterPacket;
import com.capslock.im.commons.packet.cluster.PacketType;
import com.capslock.im.commons.packet.cluster.SessionToClientClusterPacket;
import com.capslock.im.commons.packet.cluster.SessionToSessionClusterPacket;
import com.capslock.im.commons.packet.protocol.ClusterProtocol;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import java.io.IOException;

/**
 * Created by capslock1874.
 */
public final class ClusterPacketDeserializer {
    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new Jdk8Module());

    /**
     * Don't let anyone instantiate this class.
     */
    private ClusterPacketDeserializer() {
        // This constructor is intentionally empty.
    }

    public static ClusterPacket deserialize(final String rawData) throws IOException {
        final JsonNode jsonNode = objectMapper.readTree(rawData);
        final String type = jsonNode.get(ClusterProtocol.PACKET_TYPE).asText();
        final PacketType packetType = PacketType.valueOf(type);
        ClusterPacket clusterPacket;
        switch (packetType) {
            case C2S:
                clusterPacket = deserializeClientToSessionPacket(jsonNode);
                break;
            case S2C:
                clusterPacket = deserializeSessionToClientPacket(jsonNode);
                break;
            case S2S:
                clusterPacket = deserializeSessionToSessionPacket(jsonNode);
                break;
            default:
                throw new IllegalArgumentException("unknown clusterPacket type " + type);
        }
        return clusterPacket;
    }

    private static ClusterPacket deserializeClientToSessionPacket(final JsonNode jsonNode) throws IOException {
        final ClusterPacket clusterPacket;
        final ClientPeer clientPeer =
                objectMapper.readValue(jsonNode.get(ClusterProtocol.PACKET_FROM).toString(), ClientPeer.class);
        final LogicServerPeer logicServerPeer =
                objectMapper.readValue(jsonNode.get(ClusterProtocol.PACKET_TO).toString(), LogicServerPeer.class);
        final String protocolName = jsonNode.get(ClusterProtocol.PACKET_PROTOCOL_NAME).asText();
        final AbstractSocketPacket socketPacket = ProtocolPacketDeserializer
                .deserialize(protocolName, jsonNode.get(ClusterProtocol.PACKET_DATA))
                .orElseThrow(() -> new IllegalArgumentException("Illegal clusterPacket " ));
        clusterPacket = new ClientToSessionClusterPacket(clientPeer, logicServerPeer, socketPacket);
        return clusterPacket;
    }

    private static ClusterPacket deserializeSessionToSessionPacket(final JsonNode jsonNode) throws IOException {
        final ClusterPacket clusterPacket;
        final LogicServerPeer serverFrom =
                objectMapper.readValue(jsonNode.get(ClusterProtocol.PACKET_FROM).toString(), LogicServerPeer.class);
        final LogicServerPeer serverTo =
                objectMapper.readValue(jsonNode.get(ClusterProtocol.PACKET_TO).toString(), LogicServerPeer.class);
        final ClientPeer clientFrom =
                objectMapper.readValue(jsonNode.get(ClusterProtocol.MESSAGE_FROM).toString(), ClientPeer.class);
        final Long clientTo =
                objectMapper.readValue(jsonNode.get(ClusterProtocol.MESSAGE_TO).toString(), Long.class);
        final String protocolName = jsonNode.get(ClusterProtocol.PACKET_PROTOCOL_NAME).asText();
        final AbstractSocketPacket socketPacket = ProtocolPacketDeserializer
                .deserialize(protocolName, jsonNode.get(ClusterProtocol.PACKET_DATA))
                .orElseThrow(() -> new IllegalArgumentException("Illegal clusterPacket"));
        clusterPacket = new SessionToSessionClusterPacket(serverFrom, serverTo, socketPacket, clientFrom, clientTo);
        return clusterPacket;
    }

    private static ClusterPacket deserializeSessionToClientPacket(final JsonNode jsonNode) throws IOException {
        final ClusterPacket clusterPacket;
        final LogicServerPeer fromServer =
                objectMapper.readValue(jsonNode.get(ClusterProtocol.PACKET_FROM).toString(), LogicServerPeer.class);
        final ClientPeer toClient =
                objectMapper.readValue(jsonNode.get(ClusterProtocol.PACKET_TO).toString(), ClientPeer.class);
        final String protocolName = jsonNode.get(ClusterProtocol.PACKET_PROTOCOL_NAME).asText();
        final AbstractSocketPacket socketPacket = ProtocolPacketDeserializer
                .deserialize(protocolName, jsonNode.get(ClusterProtocol.PACKET_DATA))
                .orElseThrow(() -> new IllegalArgumentException("Illegal clusterPacket"));
        clusterPacket = new SessionToClientClusterPacket(fromServer, toClient, socketPacket);
        return clusterPacket;
    }
}
