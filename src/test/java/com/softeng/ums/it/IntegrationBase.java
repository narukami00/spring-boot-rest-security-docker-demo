package com.softeng.ums.it;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.mysql.MySQLContainer;

/** Shares one live MySQL container with Spring's cached context across all integration tests. */
@SpringBootTest
@AutoConfigureMockMvc
public abstract class IntegrationBase {

    // Supplies the container's mapped URL, username and password instead of localhost:3306.
    @ServiceConnection
    static final MySQLContainer DB = new MySQLContainer("mysql:8.4")
            .withStartupTimeoutSeconds(600)
            .withConnectTimeoutSeconds(30);

    static {
        DB.start();
    }

    @Autowired
    MockMvc mvc;
}
