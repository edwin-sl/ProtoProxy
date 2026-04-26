package com.cetys.protoproxy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Map;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ProxyRequest {
    protected String protocol;
    protected Optional<String> url = Optional.empty();
    protected Optional<Integer> port = Optional.empty();
    protected Optional<String> payload = Optional.empty();
    protected Optional<Map<String, String>> metadata = Optional.empty();
}
