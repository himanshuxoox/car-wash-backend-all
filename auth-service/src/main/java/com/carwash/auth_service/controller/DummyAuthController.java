package com.carwash.auth_service.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("dummy")
public class DummyAuthController {

    @GetMapping("/auth/health")
    public String health() {
        return "AUTH SERVICE IS UP";
    }
}
