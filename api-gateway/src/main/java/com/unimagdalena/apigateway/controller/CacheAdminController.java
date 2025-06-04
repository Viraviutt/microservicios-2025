package com.unimagdalena.apigateway.controller;

import com.unimagdalena.apigateway.filter.factory.ProductCachingFilterFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/cache")
public class CacheAdminController {

    @Autowired
    private ProductCachingFilterFactory cachingFilter;

    @GetMapping("/stats")
    public Mono<Map<String, Object>> getCacheStats() {
        return Mono.just(cachingFilter.getCacheStats());
    }

    @DeleteMapping("/clear")
    public Mono<Map<String, String>> clearCache() {
        cachingFilter.clearCache();
        Map<String, String> response = new HashMap<>();
        response.put("status", "Cache limpiado correctamente");
        return Mono.just(response);
    }
}