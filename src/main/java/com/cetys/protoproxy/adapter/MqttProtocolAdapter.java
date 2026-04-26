package com.cetys.protoproxy.adapter;

import com.cetys.protoproxy.Protocols;
import com.cetys.protoproxy.dto.MqttRequest;
import com.cetys.protoproxy.dto.ProtoRequest;
import com.cetys.protoproxy.dto.ProxyRequest;
import com.cetys.protoproxy.dto.ProxyResponse;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MqttProtocolAdapter extends ProtocolAdapter {

    public MqttProtocolAdapter() {
    }

    @Override
    public ProxyResponse execute(ProxyRequest request) {
        // Convert to type-safe MQTT request
        MqttRequest mqttRequest = MqttRequest.fromProxyRequest(request);

        MqttClient client = null;
        try {
            // Create MQTT broker URL
            String brokerUrl = String.format("tcp://%s:%d", mqttRequest.getUrl(), mqttRequest.getPort());

            // Create MQTT client with unique ID
            String clientId = "protoproxy-" + UUID.randomUUID();
            client = new MqttClient(brokerUrl, clientId, null);

            // Configure connection options
            MqttConnectOptions options = getMqttConnectOptions(mqttRequest);

            // Connect to broker
            client.connect(options);

            // Use type-safe fields
            client.publish(
                mqttRequest.getTopic(),
                mqttRequest.getPayload().getBytes(),
                mqttRequest.getQos(),
                mqttRequest.getRetained()
            );

            // Disconnect
            client.disconnect();
            client.close();

            return ProxyResponse.success(
                "Message published successfully to MQTT topic: " + mqttRequest.getTopic(),
                null
            );

        } catch (MqttException e) {
            return ProxyResponse.failure("MQTT error: " + e.getMessage());
        } catch (Exception e) {
            return ProxyResponse.failure("Unexpected error: " + e.getMessage());
        } finally {
            try {
                if (client != null && client.isConnected()) {
                    client.disconnect();
                    client.close();
                }
            } catch (MqttException ignored) {
            }
        }
    }

    private @NonNull MqttConnectOptions getMqttConnectOptions(MqttRequest mqttRequest) {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        options.setAutomaticReconnect(true);
        if (mqttRequest.getUsername() != null && !mqttRequest.getUsername().isEmpty()) {
            options.setUserName(mqttRequest.getUsername());
        }
        if (mqttRequest.getPassword() != null && !mqttRequest.getPassword().isEmpty()) {
            options.setPassword(mqttRequest.getPassword().toCharArray());
        }
        return options;
    }

    @Override
    public boolean supports(String protocol) {
        return protocol != null && protocol.equalsIgnoreCase("mqtt");
    }

    @Override
    public String getProtocolName() {
        return Protocols.MQTT.toString();
    }

    @Override
    public boolean validate(ProtoRequest request) {
        return request != null
            && request.getUrl() != null && !request.getUrl().isEmpty()
            && request.getPort() > 0
            && request.getPayload() != null && !request.getPayload().isEmpty();
    }
}
