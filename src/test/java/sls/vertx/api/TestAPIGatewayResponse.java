package sls.vertx.api;


import io.vertx.core.json.JsonObject;

// Import log4j classes.
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestAPIGatewayResponse {

    private final Logger logger = LogManager.getLogger(TestAPIGatewayResponse.class);

    @Test
    @Order(1)
    public void testAPIGateWayResponseRawBody() throws Throwable {

        Map<String, String> testHeaders = Collections.emptyMap();
        logger.info("Test testAPIGateWayResponseRawBody");

        ApiGatewayResponse testAPIGatewayResponse = ApiGatewayResponse.builder()
                .setHeaders(testHeaders)
                .setStatusCode(200)
                .setBase64Encoded(false)
                .setRawBody("test")
                .build();

        assertTrue(testAPIGatewayResponse.getBody().equals("test"));
        assertTrue(testAPIGatewayResponse.getStatusCode() == 200);
        assertTrue(testAPIGatewayResponse.getHeaders().isEmpty() == true);
        assertTrue(testAPIGatewayResponse.isIsBase64Encoded() == false);

    }

    @Test
    @Order(2)
    public void testAPIGateWayObjectBody() throws Throwable {

        JsonObject testObjectBody = new JsonObject();
        testObjectBody.put("test1", "value1");

        logger.info("Test testAPIGateWayResponseObjectBody");

        ApiGatewayResponse testAPIGatewayResponse = ApiGatewayResponse.builder()
                .setObjectBody(testObjectBody)
                .build();

        assertTrue(testAPIGatewayResponse.getBody().contains("test1"));
    }


}
