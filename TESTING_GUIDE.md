# Protocol Proxy - Testing Guide

## API Testing Examples

This guide provides examples for testing the Protocol Proxy API using various tools.

---

## Using cURL (Command Line)

### 1. Test MQTT Endpoint (Public Broker)

```bash
curl -X POST http://localhost:8080/api/proxy \
  -H "Content-Type: application/json" \
  -d "{
    \"protocol\": \"mqtt\",
    \"url\": \"test.mosquitto.org\",
    \"port\": 1883,
    \"metadata\": {
      \"topic\": \"protoproxy/test/message1\",
      \"payload\": \"Hello from Protocol Proxy\",
      \"qos\": \"1\",
      \"retained\": \"false\"
    }
  }"
```

### 2. Test MQTT with Complex JSON Payload

```bash
curl -X POST http://localhost:8080/api/proxy \
  -H "Content-Type: application/json" \
  -d "{
    \"protocol\": \"mqtt\",
    \"url\": \"test.mosquitto.org\",
    \"port\": 1883,
    \"metadata\": {
      \"topic\": \"sensors/building1/floor2/temperature\",
      \"payload\": \"{\\\"temperature\\\": 22.5, \\\"humidity\\\": 55, \\\"timestamp\\\": \\\"2026-04-23T10:30:00Z\\\"}\",
      \"qos\": \"1\",
      \"retained\": \"true\"
    }
  }"
```

### 3. Get Supported Protocols

```bash
curl http://localhost:8080/api/protocols
```

**Expected Output:**
```json
["mqtt","http"]
```

### 4. Test Unsupported Protocol (Error Case)

```bash
curl -X POST http://localhost:8080/api/proxy \
  -H "Content-Type: application/json" \
  -d "{
    \"protocol\": \"coap\",
    \"url\": \"example.com\",
    \"port\": 5683,
    \"payload\": \"test\"
  }"
```

**Expected Output:**
```json
{
  "success": false,
  "message": null,
  "data": null,
  "error": "Protocol 'coap' is not supported. Supported protocols: mqtt, http"
}
```

### 5. Test Missing Required Fields (Validation Error)

```bash
curl -X POST http://localhost:8080/api/proxy \
  -H "Content-Type: application/json" \
  -d "{
    \"protocol\": \"mqtt\",
    \"url\": \"test.mosquitto.org\",
    \"port\": 1883
  }"
```

**Expected Output:**
```json
{
  "success": false,
  "message": null,
  "data": null,
  "error": "Invalid request for protocol: mqtt. Required fields: url, port, and protocol-specific fields"
}
```

---

## Using Postman

### Setup

1. **Create New Request**
   - Method: POST
   - URL: `http://localhost:8080/api/proxy`
   - Headers: `Content-Type: application/json`

2. **MQTT Test**
   - Go to "Body" tab
   - Select "raw" and "JSON"
   - Paste:

```json
{
  "protocol": "mqtt",
  "url": "test.mosquitto.org",
  "port": 1883,
  "metadata": {
    "topic": "postman/test",
    "payload": "Message from Postman",
    "qos": "1",
    "retained": "false"
  }
}
```

3. **Click Send**

### Environment Variables (Optional)

Add to Postman environment:
```json
{
  "proxy_base_url": "http://localhost:8080",
  "mqtt_broker": "test.mosquitto.org",
  "mqtt_port": "1883"
}
```

Then use in requests:
```json
{
  "protocol": "mqtt",
  "url": "{{mqtt_broker}}",
  "port": "{{mqtt_port}}",
  "metadata": {
    "topic": "postman/test",
    "payload": "Message from Postman",
    "qos": "1",
    "retained": "false"
  }
}
```

---

## Using VS Code REST Client

### Install Extension

1. Open VS Code
2. Extensions → Search "REST Client" → Install by Huachao Mao

### Create test.http file

```http
### Get Supported Protocols
GET http://localhost:8080/api/protocols

### MQTT Test - Simple Message
POST http://localhost:8080/api/proxy
Content-Type: application/json

{
  "protocol": "mqtt",
  "url": "test.mosquitto.org",
  "port": 1883,
  "metadata": {
    "topic": "vscode/test",
    "payload": "Hello from VS Code",
    "qos": "1",
    "retained": "false"
  }
}

### MQTT Test - JSON Payload
POST http://localhost:8080/api/proxy
Content-Type: application/json

{
  "protocol": "mqtt",
  "url": "test.mosquitto.org",
  "port": 1883,
  "metadata": {
    "topic": "sensors/data",
    "payload": "{\"sensor\": \"temp1\", \"value\": 23.5, \"unit\": \"celsius\"}",
    "qos": "1",
    "retained": "true"
  }
}

### MQTT Test - Error Case (Missing Topic)
POST http://localhost:8080/api/proxy
Content-Type: application/json

{
  "protocol": "mqtt",
  "url": "test.mosquitto.org",
  "port": 1883,
  "metadata": {
    "payload": "No topic specified"
  }
}

### HTTP Test
POST http://localhost:8080/api/proxy
Content-Type: application/json

{
  "protocol": "http",
  "url": "httpbin.org",
  "port": 80,
  "metadata": {
    "httpProtocol": "http",
    "path": "/post",
    "payload": "{\"test\": \"data\"}"
  }
}
```

### Run Tests

- Click "Send Request" above each test
- Or: Ctrl+Alt+R to send current request

---

## Using PowerShell

### 1. Simple MQTT Request

```powershell
$body = @{
    protocol = "mqtt"
    url = "test.mosquitto.org"
    port = 1883
    payload = "Hello from PowerShell"
    metadata = @{
        topic = "powershell/test"
        qos = "1"
        retained = "false"
    }
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:8080/api/proxy" `
    -Method POST `
    -ContentType "application/json" `
    -Body $body | Select-Object -ExpandProperty Content | ConvertFrom-Json
```

### 2. Loop - Publish Multiple Messages

```powershell
1..5 | ForEach-Object {
    $body = @{
        protocol = "mqtt"
        url = "test.mosquitto.org"
        port = 1883
        payload = "Message $_"
        metadata = @{
            topic = "powershell/loop"
            qos = "1"
            retained = "false"
        }
    } | ConvertTo-Json
    
    Write-Host "Sending message $_..."
    Invoke-WebRequest -Uri "http://localhost:8080/api/proxy" `
        -Method POST `
        -ContentType "application/json" `
        -Body $body | Select-Object -ExpandProperty Content | ConvertFrom-Json | Select-Object success, message
    
    Start-Sleep -Seconds 1
}
```

### 3. Test All Endpoints

```powershell
# Get supported protocols
Write-Host "Testing GET /api/protocols"
Invoke-WebRequest -Uri "http://localhost:8080/api/protocols" | Select-Object -ExpandProperty Content

Write-Host "`nTesting POST /api/proxy with MQTT"
$body = @{
    protocol = "mqtt"
    url = "test.mosquitto.org"
    port = 1883
    payload = "Test message"
    metadata = @{
        topic = "powershell/test"
        qos = "1"
        retained = "false"
    }
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:8080/api/proxy" `
    -Method POST `
    -ContentType "application/json" `
    -Body $body | Select-Object -ExpandProperty Content | ConvertFrom-Json
```

---

## Using Python

### Simple Request

```python
import requests
import json

url = "http://localhost:8080/api/proxy"

payload = {
    "protocol": "mqtt",
    "url": "test.mosquitto.org",
    "port": 1883,
    "payload": "Hello from Python",
    "metadata": {
        "topic": "python/test",
        "qos": "1",
        "retained": "false"
    }
}

response = requests.post(url, json=payload)
print(json.dumps(response.json(), indent=2))
```

### Multiple Messages with Error Handling

```python
import requests
import json
import time

def send_proxy_request(protocol, url, port, payload, topic):
    url_endpoint = "http://localhost:8080/api/proxy"
    
    body = {
        "protocol": protocol,
        "url": url,
        "port": port,
        "payload": payload,
        "metadata": {
            "topic": topic,
            "qos": "1",
            "retained": "false"
        }
    }
    
    try:
        response = requests.post(url_endpoint, json=body, timeout=5)
        response.raise_for_status()
        return response.json()
    except requests.exceptions.RequestException as e:
        return {"error": str(e)}

# Test multiple sends
for i in range(3):
    print(f"Sending message {i+1}...")
    result = send_proxy_request(
        "mqtt",
        "test.mosquitto.org",
        1883,
        f"Test message number {i+1}",
        f"python/message{i+1}"
    )
    print(json.dumps(result, indent=2))
    time.sleep(1)
```

---

## Using JavaScript/Node.js

### Fetch API

```javascript
const proxyUrl = 'http://localhost:8080/api/proxy';

const payload = {
    protocol: 'mqtt',
    url: 'test.mosquitto.org',
    port: 1883,
    payload: 'Hello from JavaScript',
    metadata: {
        topic: 'javascript/test',
        qos: '1',
        retained: 'false'
    }
};

fetch(proxyUrl, {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json'
    },
    body: JSON.stringify(payload)
})
.then(response => response.json())
.then(data => console.log(JSON.stringify(data, null, 2)))
.catch(error => console.error('Error:', error));
```

### With Async/Await

```javascript
async function sendProxyRequest(protocol, url, port, payload, topic) {
    const response = await fetch('http://localhost:8080/api/proxy', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            protocol,
            url,
            port,
            payload,
            metadata: {
                topic,
                qos: '1',
                retained: 'false'
            }
        })
    });
    
    return await response.json();
}

// Usage
sendProxyRequest(
    'mqtt',
    'test.mosquitto.org',
    1883,
    'Hello from JavaScript',
    'js/test'
).then(result => console.log(result));
```

---

## Expected Responses

### Success Response
```json
{
  "success": true,
  "message": "Message published successfully to MQTT topic: sensors/temp",
  "data": null,
  "error": null
}
```

### Failure Response
```json
{
  "success": false,
  "message": null,
  "data": null,
  "error": "Protocol 'unknown' is not supported. Supported protocols: mqtt, http"
}
```

---

## Performance Testing

### Using Apache Bench (ab)

```bash
# Single request
ab -p mqtt_request.json -T application/json \
    -n 1 -c 1 http://localhost:8080/api/proxy

# Load test: 100 requests, 10 concurrent
ab -p mqtt_request.json -T application/json \
    -n 100 -c 10 http://localhost:8080/api/proxy
```

### File: mqtt_request.json
```json
{
  "protocol": "mqtt",
  "url": "test.mosquitto.org",
  "port": 1883,
  "metadata": {
    "topic": "benchmark/test",
    "payload": "Benchmark message",
    "qos": "1",
    "retained": "false"
  }
}
```

---

## Debugging Tips

1. **Enable Request Logging**: Add logging in `ProxyController.java`
2. **Check MQTT Connection**: Use `mosquitto_sub` to verify messages
3. **Network Issues**: Use `telnet` to test broker connectivity
4. **JSON Validation**: Use `jsonlint.com` to validate payloads

---

## Common Issues and Solutions

| Issue | Solution |
|-------|----------|
| Connection refused | Check if proxy is running: `mvn spring-boot:run` |
| Port already in use | Change port in application.properties |
| MQTT timeout | Verify broker address and port |
| JSON parse error | Validate JSON format with jsonlint |
| 400 Bad Request | Check required fields (protocol, url, port) |

---

## Next Steps

1. Verify the proxy is running
2. Run one of the test examples above
3. Check the response format
4. Integrate into your application
5. Add more protocols as needed

