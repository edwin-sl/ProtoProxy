package com.cetys.protoproxy.adapter;

import com.cetys.protoproxy.dto.ProtoRequest;
import com.cetys.protoproxy.dto.ProxyRequest;
import com.cetys.protoproxy.dto.ProxyResponse;

public abstract class ProtocolAdapter {

    protected ProtocolAdapter() {
    }
    /**
     * Execute the proxy request using this protocol
     */
    public abstract ProxyResponse execute(ProxyRequest request);

    /**
     * Check if this adapter supports the given protocol
     */
    public abstract boolean supports(String protocol);

    /**
     * Get the protocol name this adapter handles
     */
    public abstract String getProtocolName();

    /**
     * Validate if the request has all required fields for this protocol
     */
    protected abstract boolean validate(ProtoRequest request);
}

