package com.cetys.protoproxy;

public enum Protocols {
    HTTP,
    HTTPS,
    FTP,
    MQTT,
    COAP;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
