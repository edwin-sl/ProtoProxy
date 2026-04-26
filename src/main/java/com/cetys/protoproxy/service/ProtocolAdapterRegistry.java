package com.cetys.protoproxy.service;

import com.cetys.protoproxy.adapter.ProtocolAdapter;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProtocolAdapterRegistry {

    private final Map<String, ProtocolAdapter> adapters = new HashMap<>();

    /**
     * Initialize registry with available adapters
     */
    public ProtocolAdapterRegistry(List<ProtocolAdapter> protocolAdapters) {
        protocolAdapters.forEach(adapter ->
            adapters.put(adapter.getProtocolName().toLowerCase(), adapter)
        );
    }

    /**
     * Get adapter for given protocol
     */
    public ProtocolAdapter getAdapter(String protocol) {
        return adapters.get(protocol.toLowerCase());
    }

    /**
     * Check if protocol is supported
     */
    public boolean isSupported(String protocol) {
        return adapters.containsKey(protocol.toLowerCase());
    }

    /**
     * Register a new adapter manually
     */
    public void register(String protocol, ProtocolAdapter adapter) {
        adapters.put(protocol.toLowerCase(), adapter);
    }

    /**
     * Get all supported protocols
     */
    public String[] getSupportedProtocols() {
        return adapters.keySet().toArray(new String[0]);
    }
}

