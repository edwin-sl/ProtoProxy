# Protocol Proxy - Quick Start Guide

## Getting Started in 5 Minutes

### 1. Build the Project

```bash
cd C:\Users\edwin\Documents\cetys\protoproxy
mvn clean install
```

### 2. Run the Application

```bash
mvn spring-boot:run
```

Or:

```bash
java -jar target/protoproxy-0.0.1-SNAPSHOT.jar
```

The application starts on `http://localhost:8080`

### 3. Test the Proxy

#### Example 1: Publish to Free MQTT Broker (test.mosquitto.org)

```bash
curl -X POST http://localhost:8080/api/proxy ^
  -H "Content-Type: application/json" ^
  -d "{\"protocol\":\"mqtt\",\"url\":\"test.mosquitto.org\",\"port\":1883,\"metadata\":{\"topic\":\"protoproxy/test/$(Get-Date -Format 'yyyy-MM-dd-HHmmss')\",\"payload\":\"Hello from ProxyApp\",\"qos\":\"1\",\"retained\":\"false\"}}"
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Message published successfully to MQTT topic: protoproxy/test/2026-04-23-...",
  "data": null,
  "error": null
}
```

#### Example 2: Check Supported Protocols

```bash
curl http://localhost:8080/api/protocols
```

**Expected Response:**
```json
["mqtt", "http"]
```

#### Example 3: Invalid Protocol

```bash
curl -X POST http://localhost:8080/api/proxy ^
  -H "Content-Type: application/json" ^
  -d "{\"protocol\":\"coap\",\"url\":\"example.com\",\"port\":5683,\"payload\":\"test\"}"
```

**Expected Response:**
```json
{
  "success": false,
  "message": null,
  "data": null,
  "error": "Protocol 'coap' is not supported. Supported protocols: mqtt, http"
}
```

---

## Common Use Cases

### Use Case 1: IoT Sensor Data to MQTT

**Scenario:** Your IoT application wants to publish sensor data to MQTT

**Instead of:** Writing MQTT client code in every application
**Use:** Post to `/api/proxy`

**Request:**
```json
{
  "protocol": "mqtt",
  "url": "mqtt.broker.local",
  "port": 1883,
  "metadata": {
    "topic": "sensors/floor1/room101/temperature",
    "payload": "{\"temperature\": 22.5, \"humidity\": 55, \"timestamp\": \"2026-04-23T10:30:00Z\"}",
    "qos": "1",
    "retained": "true"
  }
}
```

**Benefits:**
- Single endpoint for all protocols
- No vendor lock-in
- Easy migration between brokers

---

### Use Case 2: Webhook to HTTP

**Scenario:** You want to send webhook data to an HTTP endpoint

**Request:**
```json
{
  "protocol": "http",
  "url": "api.example.com",
  "port": 443,
  "metadata": {
    "httpProtocol": "https",
    "path": "/webhooks/alerts",
    "payload": "{\"event\": \"sensor_alert\", \"severity\": \"high\"}"
  }
}
```

---

### Use Case 3: Multi-Protocol Router

**Scenario:** Your system needs to route data to different protocols based on configuration

**Solution:** Use the same proxy endpoint with dynamic protocol selection

```bash
# Send to MQTT
curl -X POST http://localhost:8080/api/proxy \
  -H "Content-Type: application/json" \
  -d @mqtt-request.json

# Send to HTTP  
curl -X POST http://localhost:8080/api/proxy \
  -H "Content-Type: application/json" \
  -d @http-request.json
```

---

## File References

- **Main Application:** `src/main/java/com/cetys/protoproxy/ProtoproxyApplication.java`
- **REST Endpoint:** `src/main/java/com/cetys/protoproxy/ProxyController.java`
- **Protocol Routing:** `src/main/java/com/cetys/protoproxy/service/ProxyService.java`
- **MQTT Adapter:** `src/main/java/com/cetys/protoproxy/adapter/MqttProtocolAdapter.java`
- **HTTP Adapter:** `src/main/java/com/cetys/protoproxy/adapter/HttpProtocolAdapter.java`
- **Full Documentation:** `PROTOCOL_PROXY_README.md`
- **Architecture Details:** `PROJECT_STRUCTURE.md`

---

## Troubleshooting

### Issue: "Cannot resolve symbol" in IDE

**Solution:** 
- Run `mvn clean install` to regenerate with Lombok
- Rebuild project in IDE (usually Ctrl+F9 in IntelliJ)
- The error won't appear once dependencies are resolved

### Issue: MQTT Connection Refused

**Solution:**
- Verify broker address is correct
- Verify port is accessible
- For testing, use `test.mosquitto.org` on port 1883

### Issue: HTTP Request Timeout

**Solution:**
- Verify URL/port is correct
- Increase RestTemplate timeout if needed (modify `HttpProtocolAdapter.java`)

---

## Next Steps

1. **Test with your own MQTT broker** - Replace test.mosquitto.org with your broker
2. **Add new protocol adapter** - Create a new class implementing `ProtocolAdapter`
3. **Integrate into your system** - Use as a middleware between your services
4. **Deploy** - Package as Docker container or WAR file

---

## Example Projects to Try

### Send data to public MQTT broker
```bash
curl -X POST http://localhost:8080/api/proxy \
  -H "Content-Type: application/json" \
  -d "{
    \"protocol\": \"mqtt\",
    \"url\": \"test.mosquitto.org\",
    \"port\": 1883,
    \"metadata\": {
      \"topic\": \"my/test/topic\",
      \"payload\": \"Test message\",
      \"qos\": \"1\",
      \"retained\": \"false\"
    }
  }"
```

### Add a new CoAP Protocol

Create `src/main/java/com/cetys/protoproxy/adapter/CoapProtocolAdapter.java`:

```java
@Component
public class CoapProtocolAdapter implements ProtocolAdapter {
    
    @Override
    public ProxyResponse execute(ProxyRequest request) {
        // TODO: Implement CoAP logic
        return ProxyResponse.success("CoAP message sent", null);
    }
    
    @Override
    public boolean supports(String protocol) {
        return "coap".equalsIgnoreCase(protocol);
    }
    
    @Override
    public String getProtocolName() {
        return "coap";
    }
    
    @Override
    public boolean validate(ProxyRequest request) {
        return request != null && request.getUrl() != null && request.getPort() > 0;
    }
}
```

That's it! The new protocol will automatically be available.

---

## Support

For issues or questions:
1. Check `PROTOCOL_PROXY_README.md` for detailed API documentation
2. Check `PROJECT_STRUCTURE.md` for architecture details
3. Review adapter implementations for examples
4. Run `mvn test` to verify everything works

