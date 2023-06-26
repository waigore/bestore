package com.example.estore.util;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class IdUtil {
    public UUID generateNewIdentifier() {
        return UUID.randomUUID();
    }
}
