package com.unimagdalena.apigateway.controller;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/cache")
public class CacheAdminController {

    @GetMapping("/stats")
    public Mono<Map<String, Object>> getCacheStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("message", "No disponible: el caché ahora está en Redis.");
        return Mono.just(stats);
    }

    @DeleteMapping("/clear")
    public Mono<Map<String, String>> clearCache() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "No disponible: el caché ahora está en Redis.");
        return Mono.just(response);
    }
}