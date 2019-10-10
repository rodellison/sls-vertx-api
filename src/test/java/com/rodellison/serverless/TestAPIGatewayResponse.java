package com.rodellison.serverless;

import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import io.vertx.core.json.JsonObject;
import org.junit.Assert;
import org.apache.log4j.Logger;
import org.junit.Assert.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.jupiter.api.*;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.util.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestAPIGatewayResponse {

    private final Logger logger = Logger.getLogger(TestAPIGatewayResponse.class);

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

        Assert.assertTrue(testAPIGatewayResponse.getBody().equals("test"));
        Assert.assertTrue(testAPIGatewayResponse.getStatusCode() == 200);
        Assert.assertTrue(testAPIGatewayResponse.getHeaders().isEmpty() == true);
        Assert.assertTrue(testAPIGatewayResponse.isBase64Encoded() == false);

    }

    @Test
    @Order(2)
    public void testAPIGateWayObjectBody() throws Throwable {

        JsonObject testObjectBody = new JsonObject();
        testObjectBody.put("test1", "value1");

        logger.info("Test testAPIGateWayResponseRawBody");

        ApiGatewayResponse testAPIGatewayResponse = ApiGatewayResponse.builder()
                .setObjectBody(testObjectBody)
                .build();

        Assert.assertTrue(testAPIGatewayResponse.getBody().contains("test1"));
    }


}
