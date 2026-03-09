package com.careapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/")
public class HealthController {

    // Root path, prevents 404 when accessing Heroku domain
    @GetMapping
    public String index() {
        return "CareApp Backend is running!";
    }

    // Health check endpoint, for frontend/monitoring calls
    @GetMapping("/api/health")
    public Map<String, Object> health() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "UP");
        status.put("service", "careapp-backend");
        status.put("timestamp", Instant.now().toString());
        return status;
    }
}
