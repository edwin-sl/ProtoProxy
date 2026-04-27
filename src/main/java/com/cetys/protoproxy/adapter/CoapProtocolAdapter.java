package com.cetys.protoproxy.adapter;

import com.cetys.protoproxy.Protocols;
import com.cetys.protoproxy.dto.CoapRequest;
import com.cetys.protoproxy.dto.ProtoRequest;
import com.cetys.protoproxy.dto.ProxyRequest;
import com.cetys.protoproxy.dto.ProxyResponse;
import com.mbed.coap.client.CoapClient;
import com.mbed.coap.packet.CoapResponse;
import com.mbed.coap.server.CoapServer;
import org.springframework.stereotype.Component;
import java.net.InetSocketAddress;
import static com.mbed.coap.transport.udp.DatagramSocketTransport.udp;

@Component
public class CoapProtocolAdapter extends ProtocolAdapter {

    @Override
    public ProxyResponse execute(ProxyRequest request) {
        try {
            CoapRequest coapRequest = CoapRequest.fromProxyRequest(request);

            // Build the CoAP URI
            String uri = String.format("coap://%s:%d%s", coapRequest.getUrl(), coapRequest.getPort(), coapRequest.getPath());

            // Create CoapClient
            CoapClient client = CoapServer.builder()
                    .transport(udp())
                    .buildClient(new InetSocketAddress(coapRequest.getUrl(), coapRequest.getPort()));

            // Create the request
            com.mbed.coap.packet.CoapRequest.Builder requestBuilder;
            switch (coapRequest.getMethod().toUpperCase()) {
                case "GET":
                    requestBuilder = com.mbed.coap.packet.CoapRequest
                            .get(coapRequest.getPath());
                    break;
                case "POST":
                    requestBuilder = com.mbed.coap.packet.CoapRequest
                            .post(coapRequest.getPath())
                            .payload(coapRequest.getPayload().getBytes());
                    break;
                case "PUT":
                    requestBuilder = com.mbed.coap.packet.CoapRequest
                            .put(coapRequest.getPath())
                            .payload(coapRequest.getPayload().getBytes());
                    break;
                case "DELETE":
                    requestBuilder = com.mbed.coap.packet.CoapRequest
                            .delete(coapRequest.getPath());
                    break;
                default:
                    return ProxyResponse.failure("Unsupported CoAP method: " + coapRequest.getMethod());
            }

            // Send the request
            CoapResponse response = client.sendSync(requestBuilder.build());

            if (response != null) {
                String responseText = response.getPayloadString();
                return ProxyResponse.success(
                    "CoAP request forwarded successfully to: " + uri,
                    responseText != null ? responseText : "No response payload"
                );
            } else {
                return ProxyResponse.failure("No response received from CoAP server");
            }

        } catch (Exception e) {
            return ProxyResponse.failure("CoAP error: " + e.getMessage());
        }
    }

    @Override
    public boolean supports(String protocol) {
        return protocol != null && protocol.equalsIgnoreCase("coap");
    }

    @Override
    public String getProtocolName() {
        return Protocols.COAP.toString();
    }

    @Override
    public boolean validate(ProtoRequest request) {
        return request != null
            && request.getUrl() != null && !request.getUrl().isEmpty()
            && request.getPort() > 0;
    }
}
