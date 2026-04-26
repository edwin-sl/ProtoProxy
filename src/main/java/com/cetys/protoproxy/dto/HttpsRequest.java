package com.cetys.protoproxy.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.Collections;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class HttpsRequest extends ProtoRequest {
    private String method;
    private String path;
    private String contentType;

    public static HttpsRequest fromProxyRequest(ProxyRequest request) {
        if (!"http".equalsIgnoreCase(request.getProtocol()) && !"https".equalsIgnoreCase(request.getProtocol())) {
            throw new IllegalArgumentException("Request protocol must be HTTP or HTTPS");
        }

        return HttpsRequest.builder()
                .protocol(request.getProtocol())
                .url(request.getUrl().orElse("/"))
                .port(request.getPort().orElse(443))
                .payload(request.getMetadata().orElse(Collections.emptyMap()).getOrDefault("payload", ""))
                .method(request.getMetadata().orElse(Collections.emptyMap()).getOrDefault("method", "GET"))
                .path(request.getMetadata().orElse(Collections.emptyMap()).getOrDefault("path", "/"))
                .contentType("application/json") // Default, could be extracted from metadata
                .build();
    }
}
