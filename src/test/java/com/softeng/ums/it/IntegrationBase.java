package com.softeng.ums.it;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.mysql.MySQLContainer;

/**
 * Shared base for all integration tests, using the SINGLETON CONTAINER pattern.
 *
 * Why not @Testcontainers + @Container? That lifecycle is per class: it stops the
 * container after each class. Spring then REUSES the cached context for the next
 * class (the config is identical) — but that context still points at the stopped
 * container, so every query times out and the controller returns 500. Chat logs
 * proved it: in isolation each class passes; back-to-back, the second class dies.
 *
 * The singleton pattern starts the container once in a static block and never
 * stops it manually — the JVM shutting down takes the container with it, and
 * Spring's context caching reuses one live container across every *IT class.
 *
 * The image is deliberately mysql:8.4 — the same one compose.yaml uses to run
 * the app locally, so a CI pass means exactly what a local pass means.
 *
 * @ServiceConnection makes Boot rewire the datasource to this container at
 * startup, overriding the dead localhost:3306 in application.properties.
 */
@SpringBootTest(
        // MySQL's docker entrypoint runs a temporary server during first-boot
        // init, then stops it and starts the real one. Testcontainers' probe
        // can succeed in that window while the port is briefly refused; without
        // a pre-set dialect, Hibernate's very next connection attempt dies there.
        // Presetting the dialect lets Hibernate boot with connection work done
        // later through the pool, whose retries cover the gap.
        properties = "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect"
)
@AutoConfigureMockMvc
public abstract class IntegrationBase {

    // One container for the whole JVM. The long startup allowance is not a
    // luxury: on some machines (Windows + WSL2 port forwarding) MySQL's first
    // boot takes minutes, and Testcontainers' default JDBC readiness probe
    // would otherwise give up and report "container started but cannot be
    // accessed". CI on Linux does not need this, but the allowance is harmless.
    static MySQLContainer DB = new MySQLContainer("mysql:8.4")
            .withStartupTimeoutSeconds(600)
            .withConnectTimeoutSeconds(30);

    static {
        DB.start();
    }

    @Autowired
    MockMvc mvc;
}
