package com.cetys.protoproxy.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.Collections;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class CoapRequest extends ProtoRequest {
    private String method;
    private String path;

    public static CoapRequest fromProxyRequest(ProxyRequest request) {
        if (!"coap".equalsIgnoreCase(request.getProtocol())) {
            throw new IllegalArgumentException("Request protocol must be COAP");
        }

        return CoapRequest.builder()
                .protocol(request.getProtocol())
                .url(request.getUrl().orElse("/"))
                .port(request.getPort().orElse(5683)) // Default CoAP port
                .payload(request.getMetadata().orElse(Collections.emptyMap()).getOrDefault("payload", ""))
                .method(request.getMetadata().orElse(Collections.emptyMap()).getOrDefault("method", "GET"))
                .path(request.getMetadata().orElse(Collections.emptyMap()).getOrDefault("path", "/"))
                .build();
    }
}
