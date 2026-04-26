package com.cetys.protoproxy.service;

import com.cetys.protoproxy.adapter.ProtocolAdapter;
import com.cetys.protoproxy.dto.ProxyRequest;
import com.cetys.protoproxy.dto.ProxyResponse;
import org.springframework.stereotype.Service;

@Service
public class ProxyService {

    private final ProtocolAdapterRegistry registry;

    public ProxyService(ProtocolAdapterRegistry registry) {
        this.registry = registry;
    }

    /**
     * Process a proxy request by routing to the appropriate protocol adapter
     */
    public ProxyResponse processRequest(ProxyRequest request) {
        // Validate input
        if (request == null || request.getProtocol() == null || request.getProtocol().isEmpty()) {
            return ProxyResponse.failure("Protocol field is required");
        }

        // Get the adapter for the protocol
        ProtocolAdapter adapter = registry.getAdapter(request.getProtocol());
        if (adapter == null) {
            String supportedProtocols = String.join(", ", registry.getSupportedProtocols());
            return ProxyResponse.failure(
                String.format("Protocol '%s' is not supported. Supported protocols: %s",
                    request.getProtocol(), supportedProtocols)
            );
        }
        // Execute the request using the appropriate adapter
        return adapter.execute(request);
    }

    /**
     * Get list of supported protocols
     */
    public String[] getSupportedProtocols() {
        return registry.getSupportedProtocols();
    }
}


