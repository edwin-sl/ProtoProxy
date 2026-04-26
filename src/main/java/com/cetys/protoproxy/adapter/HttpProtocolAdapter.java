package com.cetys.protoproxy.adapter;

import com.cetys.protoproxy.Protocols;
import com.cetys.protoproxy.dto.HttpRequest;
import com.cetys.protoproxy.dto.ProtoRequest;
import com.cetys.protoproxy.dto.ProxyRequest;
import com.cetys.protoproxy.dto.ProxyResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class HttpProtocolAdapter extends ProtocolAdapter {

    private final RestTemplate restTemplate;

    public HttpProtocolAdapter() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public ProxyResponse execute(ProxyRequest request) {

        try {
            HttpRequest httpRequest = HttpRequest.fromProxyRequest(request);
            // Build the URL
            String protocol = httpRequest.getProtocol();
            String url = String.format("%s://%s:%d", protocol, httpRequest.getUrl(), httpRequest.getPort());

            // Add path if provided in metadata
            if (!httpRequest.getPath().isEmpty()) {
                url += httpRequest.getPath();
            }

            // Send POST request with payload
            String response = restTemplate.postForObject(url, request.getPayload(), String.class);

            return ProxyResponse.success(
                "HTTP request forwarded successfully to: " + url,
                response
            );

        } catch (RestClientException e) {
            return ProxyResponse.failure("HTTP error: " + e.getMessage());
        } catch (Exception e) {
            return ProxyResponse.failure("Unexpected error: " + e.getMessage());
        }
    }

    @Override
    public boolean supports(String protocol) {
        return protocol != null && (protocol.equalsIgnoreCase("http") || protocol.equalsIgnoreCase("https"));
    }

    @Override
    public String getProtocolName() {
        return Protocols.HTTP.toString();
    }

    @Override
    public boolean validate(ProtoRequest request) {
        return request != null
            && request.getUrl() != null && !request.getUrl().isEmpty()
            && request.getPort() > 0
            && request.getPayload() != null && !request.getPayload().isEmpty();
    }
}

