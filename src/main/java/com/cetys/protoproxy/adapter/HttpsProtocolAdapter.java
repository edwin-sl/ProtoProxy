package com.cetys.protoproxy.adapter;

import com.cetys.protoproxy.Protocols;
import com.cetys.protoproxy.dto.*;
import org.springframework.stereotype.Component;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;

@Component
public class HttpsProtocolAdapter extends ProtocolAdapter {

    public HttpsProtocolAdapter() {
    }

    @Override
    public ProxyResponse execute(ProxyRequest request) {

        try {
            HttpsRequest httpRequestData = HttpsRequest.fromProxyRequest(request);
            // Build the URL
            String protocol = httpRequestData.getProtocol();
            String url = String.format("%s://%s:%d", protocol, httpRequestData.getUrl(), httpRequestData.getPort());

            // Add path if provided in metadata
            if (!httpRequestData.getPath().isEmpty()) {
                url += httpRequestData.getPath();
            }

            // Configure request
            java.net.http.HttpRequest httpRequest = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create(url))
                    .method(httpRequestData.getMethod(), java.net.http.HttpRequest.BodyPublishers.ofString(httpRequestData.getPayload()))
                    .header("Content-Type", httpRequestData.getContentType())
                    .build();


            // Send request with payload
            HttpResponse<String> response = HttpClient.newBuilder()
                    .build()
                    .send(httpRequest, HttpResponse.BodyHandlers.ofString());

            return ProxyResponse.success(
                "HTTP request forwarded successfully to: " + url,
                response.body()
            );

        } catch (InterruptedException e) {
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
        return Protocols.HTTPS.toString();
    }

    @Override
    public boolean validate(ProtoRequest request) {
        return request != null
            && request.getUrl() != null && !request.getUrl().isEmpty()
            && request.getPort() > 0
            && request.getPayload() != null && !request.getPayload().isEmpty();
    }
}

