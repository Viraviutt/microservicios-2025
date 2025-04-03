package com.unimagdalena.orderservice;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    // Use a specific version for reproducible tests
    private static final String POSTGRES_VERSION = "15-alpine";

    // Singleton container shared between test methods
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:" + POSTGRES_VERSION)
    )
            .withDatabaseName("orders_test")
            .withUsername("test_user")
            .withPassword("test_password")
            .withReuse(true)
            .withLabel("owner", "order-service")
            .withLabel("purpose", "integration-testing");

    static {
        // Start the container once and reuse it to improve test execution time
        postgres.start();
    }

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return postgres;
    }

    // Optional: Add other test configuration beans here
    @Bean
    public String testConfigInfo() {
        return "Test database is running at: " +
                postgres.getHost() + ":" + postgres.getFirstMappedPort();
    }
}
