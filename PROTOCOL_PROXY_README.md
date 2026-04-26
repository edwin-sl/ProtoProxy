# Protocol Proxy Application

A flexible, extensible Spring Boot application that acts as a proxy for multiple protocols. Send a single HTTP POST request and have it automatically routed to MQTT, HTTP, or other protocols.

## Architecture

The application uses the **Adapter Pattern** to support multiple protocols:

- **ProtocolAdapter Interface**: Defines the contract for protocol implementations
- **Protocol Adapters**: Concrete implementations for each protocol (MQTT, HTTP, etc.)
- **ProtocolAdapterRegistry**: Service that registers and retrieves adapters
- **ProxyService**: Orchestrates protocol selection and execution
- **ProxyController**: REST endpoints for the proxy
- **DTO Hierarchy**: Generic `ProxyRequest` with protocol-specific extensions (`MqttRequest`, `HttpRequest`, etc.)

## DTO Design: Hybrid Approach

The application uses a **hybrid DTO design** that combines flexibility with type safety:

### Generic ProxyRequest (Base)
- Flexible metadata map for protocol-specific fields
- Default port assignment based on protocol
- Helper methods for common metadata access
- Backward compatible with all existing clients

### Protocol-Specific DTOs (Extensions)
- Type-safe classes extending `ProtoRequest`
- Strongly-typed protocol-specific fields
- Better IDE support and validation
- Used internally by adapters for type safety

### Static Factory Methods
- Each protocol DTO has a `fromProxyRequest()` static method
- Converts generic `ProxyRequest` to specific DTOs
- Maintains backward compatibility
- Enables gradual migration to type-safe DTOs

**Benefits**: Maximum flexibility for new protocols + type safety for complex ones

## Currently Supported Protocols

- **MQTT**: Publish messages to MQTT brokers

## API Endpoints

### 1. POST /api/proxy
Send a proxy request. The request body structure varies by protocol.

#### MQTT Example Request

```json
{
  "protocol": "mqtt",
  "url": "mqtt.example.com",
  "port": 1883,
  "metadata": {
    "topic": "home/sensors/temperature",
    "payload": "{\"temperature\": 22.5, \"humidity\": 60}",
    "qos": "1",
    "retained": "false",
    "username": "optional_user",
    "password": "optional_pass"
  }
}
```

**MQTT Request Fields:**
- `protocol` (required): "mqtt"
- `url` (required): MQTT broker address
- `port` (required): MQTT broker port (default: 1883)
- `metadata` (required): Protocol-specific configuration
  - `topic` (required): MQTT topic to publish to
  - `payload` (required): Message payload to publish
  - `qos` (optional): Quality of Service - "0", "1", or "2". Default: "1"
  - `retained` (optional): Whether the message should be retained. Default: "false"
  - `username` (optional): MQTT broker username for authentication
  - `password` (optional): MQTT broker password for authentication

#### Response Example

```json
{
  "success": true,
  "message": "Message published successfully to MQTT topic: home/sensors/temperature",
  "data": null,
  "error": null
}
```

#### Error Response Example

```json
{
  "success": false,
  "message": null,
  "data": null,
  "error": "Protocol 'unknown' is not supported. Supported protocols: mqtt"
}
```

### 2. GET /api/protocols
Get list of all supported protocols.

```bash
curl http://localhost:8080/api/protocols
```

Response:
```json
["mqtt"]
```

## Usage Examples

### Using cURL

**Publish to MQTT:**
```bash
curl -X POST http://localhost:8080/api/proxy \
  -H "Content-Type: application/json" \
  -d '{
    "protocol": "mqtt",
    "url": "mqtt.example.com",
    "port": 1883,
    "metadata": {
      "topic": "sensors/temp",
      "payload": "25.5",
      "qos": "1",
      "retained": "false"
    }
  }'
```

**Get supported protocols:**
```bash
curl http://localhost:8080/api/protocols
```

### Using Postman

1. Create a new POST request to `http://localhost:8080/api/proxy`
2. Set Content-Type to `application/json`
3. Use the JSON body examples above

## Adding a New Protocol

To add support for a new protocol (e.g., HTTP, CoAP):

1. Create a new class implementing `ProtocolAdapter` in `com.cetys.protoproxy.adapter`
2. Implement the required methods:
   - `execute(ProxyRequest)`: Execute the protocol-specific logic
   - `supports(String protocol)`: Check if this adapter handles the protocol
   - `getProtocolName()`: Return the protocol name
   - `validate(ProxyRequest)`: Validate required fields for this protocol

3. Mark the class with `@Component` so Spring registers it automatically

Example:

```java
@Component
public class HttpProtocolAdapter implements ProtocolAdapter {
    
    @Override
    public ProxyResponse execute(ProxyRequest request) {
        // Implement HTTP forwarding logic
        return ProxyResponse.success("HTTP request forwarded", null);
    }
    
    @Override
    public boolean supports(String protocol) {
        return protocol != null && protocol.equalsIgnoreCase("http");
    }
    
    @Override
    public String getProtocolName() {
        return "http";
    }
    
    @Override
    public boolean validate(ProxyRequest request) {
        return request != null && request.getUrl() != null && request.getPort() > 0;
    }
}
```

## Configuration

### MQTT Configuration

Default MQTT settings:
- Connection attempt timeout: default from Paho client
- Clean session: enabled
- Automatic reconnect: enabled
- Disconnect after publish: yes

### Application Properties

Edit `src/main/resources/application.properties`:

```properties
server.port=8080
spring.application.name=protoproxy
```

## Building and Running

### Build

```bash
mvn clean install
```

### Run

```bash
mvn spring-boot:run
```

Or if already built:

```bash
java -jar target/protoproxy-0.0.1-SNAPSHOT.jar
```

The application will start on `http://localhost:8080`

## Dependencies

- Spring Boot 3.x
- Eclipse Paho MQTT Client (1.2.5)
- Lombok (code generation)
- Java 21

## Error Handling

All errors are returned in a consistent format:

```json
{
  "success": false,
  "message": null,
  "data": null,
  "error": "Error description here"
}
```

Common errors:
- Missing or NULL protocol field
- Unsupported protocol
- Missing required fields for the protocol
- Connection/network errors specific to the protocol

## Future Enhancements

- [ ] HTTP/HTTPS protocol adapter
- [ ] CoAP protocol adapter
- [ ] Async/background task execution
- [ ] Connection pooling for protocols that support it
- [ ] Rate limiting and throttling
- [ ] Request/response logging and audit trail
- [ ] Authentication and authorization
- [ ] Protocol-specific configuration via properties file

