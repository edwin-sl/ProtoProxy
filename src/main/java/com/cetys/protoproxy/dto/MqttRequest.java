package com.cetys.protoproxy.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.Collections;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class MqttRequest extends ProtoRequest {
    private String topic;
    private Integer qos;
    private Boolean retained;
    private String username;
    private String password;

    // Factory method to create from ProxyRequest
    public static MqttRequest fromProxyRequest(ProxyRequest request) {
        if (!"mqtt".equalsIgnoreCase(request.getProtocol())) {
            throw new IllegalArgumentException("Request protocol must be MQTT");
        }

        return MqttRequest.builder()
                .protocol(request.getProtocol())
                .url(request.getUrl().orElse("/"))
                .port(request.getPort().orElse(1883))
                .topic(request.getMetadata().orElse(Collections.emptyMap()).getOrDefault("topic", ""))
                .payload(request.getMetadata().orElse(Collections.emptyMap()).getOrDefault("payload", ""))
                .qos(Integer.parseInt(request.getMetadata().orElse(Collections.emptyMap()).getOrDefault("qos", "1")))
                .retained(Boolean.parseBoolean(request.getMetadata().orElse(Collections.emptyMap()).getOrDefault("retained", "false")))
                .username(request.getMetadata().orElse(Collections.emptyMap()).getOrDefault("username", ""))
                .password(request.getMetadata().orElse(Collections.emptyMap()).getOrDefault("password", ""))
                .build();
    }
}
