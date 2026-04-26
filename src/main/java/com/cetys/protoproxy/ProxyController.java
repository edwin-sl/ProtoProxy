package com.cetys.protoproxy;

import com.cetys.protoproxy.dto.ProxyRequest;
import com.cetys.protoproxy.dto.ProxyResponse;
import com.cetys.protoproxy.service.ProxyService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ProxyController {

    private final ProxyService proxyService;

    public ProxyController(ProxyService proxyService) {
        this.proxyService = proxyService;
    }

    @PostMapping("/proxy")
    public ProxyResponse handleProxy(@RequestBody ProxyRequest request) {
        return proxyService.processRequest(request);
    }

    @GetMapping("/protocols")
    public String[] getSupportedProtocols() {
        return proxyService.getSupportedProtocols();
    }
}

