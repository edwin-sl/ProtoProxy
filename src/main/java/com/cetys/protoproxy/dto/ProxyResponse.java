package com.cetys.protoproxy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProxyResponse {
    private boolean success;
    private String message;
    private Object data;
    private String error;

    public static ProxyResponse success(String message, Object data) {
        return ProxyResponse.builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    public static ProxyResponse failure(String error) {
        return ProxyResponse.builder()
                .success(false)
                .error(error)
                .build();
    }
}

