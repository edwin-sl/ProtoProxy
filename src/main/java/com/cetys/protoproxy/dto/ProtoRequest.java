package com.cetys.protoproxy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class ProtoRequest {
    protected String protocol;
    protected String url;
    protected Integer port;
    protected String payload;

    static ProtoRequest fromProxyRequest(ProxyRequest proxyRequest) {
        return null;
    }
}
