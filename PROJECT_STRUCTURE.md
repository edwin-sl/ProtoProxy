# Protocol Proxy - Project Structure

## Generated File Structure

```
src/main/java/com/cetys/protoproxy/
├── ProtoproxyApplication.java          (Main Spring Boot application)
├── ProxyController.java                (REST endpoints)
│
├── adapter/
│   ├── ProtocolAdapter.java            (Interface for all protocol adapters)
│   ├── MqttProtocolAdapter.java        (MQTT implementation)
│   └── HttpProtocolAdapter.java        (HTTP implementation - example)
│
├── service/
│   ├── ProtocolAdapterRegistry.java    (Adapter registry/discovery)
│   └── ProxyService.java               (Orchestration service)
│
└── dto/
    ├── ProxyRequest.java               (Request DTO - generic base)
    ├── ProxyResponse.java              (Response DTO)
    ├── MqttRequest.java                (MQTT-specific DTO - extends ProxyRequest)
    ├── HttpRequest.java                (HTTP-specific DTO - extends ProxyRequest)
    └── ProtocolRequestFactory.java     (Factory for DTO conversion)
```

## Key Components

### 1. **ProtocolAdapter Interface** (`adapter/ProtocolAdapter.java`)
   - Defines the contract all protocol adapters must implement
   - Methods: `execute()`, `supports()`, `getProtocolName()`, `validate()`

### 2. **MqttProtocolAdapter** (`adapter/MqttProtocolAdapter.java`)
   - Handles MQTT protocol requests
   - Features:
     - Connects to MQTT broker
     - Publishes messages with configurable QoS
     - Supports message retention
     - Proper connection cleanup

### 3. **HttpProtocolAdapter** (`adapter/HttpProtocolAdapter.java`)
   - Example of adding a new protocol
   - Handles HTTP/HTTPS requests
   - Shows how to extend the system

### 4. **ProtocolAdapterRegistry** (`service/ProtocolAdapterRegistry.java`)
   - Auto-discovers and registers all `@Component` adapters
   - Provides protocol lookup and validation
   - Can manually register new adapters

### 5. **ProxyService** (`service/ProxyService.java`)
   - Orchestrates protocol selection
   - Validates requests
   - Routes to appropriate adapter
   - Returns consistent responses

### 6. **ProxyController** (`ProxyController.java`)
   - Exposes REST endpoints
   - `POST /api/proxy` - main proxy endpoint
   - `GET /api/protocols` - list supported protocols

### 7. **DTOs**
   - **ProxyRequest**: Request payload with protocol, url, port, payload, and metadata
   - **ProxyResponse**: Consistent response format (success, message, data, error)
   - **MqttRequest**: MQTT-specific request DTO, extends `ProxyRequest`
   - **HttpRequest**: HTTP-specific request DTO, extends `ProxyRequest`
   - **ProtocolRequestFactory**: Factory class to convert and create request DTOs

## Design Patterns Used

1. **Adapter Pattern**: Each protocol has its own adapter implementing `ProtocolAdapter`
2. **Factory Pattern**: `ProtocolAdapterRegistry` acts as a factory for adapters
3. **Dependency Injection**: Spring automatically injects all adapters into registry
4. **Strategy Pattern**: Different strategies (adapters) for different protocols

## Extension Points

To add a new protocol (e.g., CoAP, AMQP, WebSocket):

1. Create a new class in `adapter/` package
2. Implement `ProtocolAdapter` interface
3. Annotate with `@Component`
4. No other code needs to be modified - automatic discovery!

## Dependencies Added

```xml
<!-- pom.xml additions -->
<dependency>
    <groupId>org.eclipse.paho</groupId>
    <artifactId>org.eclipse.paho.client.mqttv3</artifactId>
    <version>1.2.5</version>
</dependency>
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```

## Building the Project

```bash
# Build all dependencies and compile
mvn clean install

# Run the application
mvn spring-boot:run

# Run tests
mvn test
```

## Testing the API

### Test MQTT endpoint

```bash
curl -X POST http://localhost:8080/api/proxy \
  -H "Content-Type: application/json" \
  -d '{
    "protocol": "mqtt",
    "url": "test.mosquitto.org",
    "port": 1883,
    "topic": "protoproxy/test",
    "payload": "{\"test\": true}",
    "qos": 1,
    "retained": false
  }'
```

### Test HTTP endpoint

```bash
curl -X POST http://localhost:8080/api/proxy \
  -H "Content-Type: application/json" \
  -d '{
    "protocol": "http",
    "url": "httpbin.org",
    "port": 80,
    "payload": "{\"test\": true}",
    "metadata": {
      "path": "/post"
    }
  }'
```

### Get supported protocols

```bash
curl http://localhost:8080/api/protocols
```

## Features

✅ Multi-protocol support with extensible adapter pattern
✅ Automatic adapter discovery via Spring component scanning
✅ Consistent request/response format
✅ Proper error handling and messaging
✅ Protocol-specific validation
✅ Clean separation of concerns
✅ Easy to add new protocols without modifying existing code
✅ Support for protocol-specific settings (QoS for MQTT, auth for HTTP, etc.)

## Future Enhancements

- [ ] Async request execution
- [ ] Connection pooling/caching
- [ ] Rate limiting
- [ ] Request/response logging
- [ ] Authentication & Authorization
- [ ] Protocol-specific configuration files
- [ ] WebSocket adapter
- [ ] AMQP adapter
- [ ] CoAP adapter

