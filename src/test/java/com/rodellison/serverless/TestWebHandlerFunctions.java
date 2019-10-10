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
public class TestWebHandlerFunctions {

    private final Logger logger = Logger.getLogger(TestWebHandlerFunctions.class);
    private static ServiceLauncher sl;
    private Context testContext;

    @BeforeClass
    public static void setUp() throws IOException {

        //This call establishes service front door, and starts vertx
        sl = new ServiceLauncher();
    }

    @Test
    @Order(1)
    public void testWebHandlerGetUsersNoName() throws Throwable {

        Map<String, Object> map = new HashMap<>();
        map.put("httpMethod", "GET");
        map.put("resource", "/users");

        ApiGatewayResponse theAPIGateWayResponse;

        logger.info("Test WebHandlerVerticle responds for GET:/users");
        theAPIGateWayResponse = sl.handleRequest(map, testContext);
        Assert.assertTrue(theAPIGateWayResponse.getBody().contains("Received GET:/users"));

    }

    @Test
    @Order(2)
    public void testWebHandlerGetUsersWithName() throws Throwable {

        Map<String, Object> map = new HashMap<>();
        map.put("httpMethod", "GET");
        map.put("resource", "/users/{id}");

        ApiGatewayResponse theAPIGateWayResponse;

        logger.info("Test WebHandlerVerticle responds for GET:/users/{id}");
        theAPIGateWayResponse = sl.handleRequest(map, testContext);
        Assert.assertTrue(theAPIGateWayResponse.getBody().contains("{id}"));

    }

    @Test
    @Order(3)
    public void testWebHandlerPostUsers() throws Throwable {

        Map<String, Object> map = new HashMap<>();
        map.put("httpMethod", "POST");
        map.put("resource", "/users");

        ApiGatewayResponse theAPIGateWayResponse;

        logger.info("Test WebHandlerVerticle responds for POST:/users");
        theAPIGateWayResponse = sl.handleRequest(map, testContext);
        Assert.assertTrue(theAPIGateWayResponse.getBody().contains("Received POST:/users"));

    }

    @Test
    @Order(4)
    public void testWebHandlerPutUsersWithName() throws Throwable {

        Map<String, Object> map = new HashMap<>();
        map.put("httpMethod", "PUT");
        map.put("resource", "/users/{id}");

        ApiGatewayResponse theAPIGateWayResponse;

        logger.info("Test WebHandlerVerticle responds for Post:/users");
        theAPIGateWayResponse = sl.handleRequest(map, testContext);
        Assert.assertTrue(theAPIGateWayResponse.getBody().contains("Received PUT:/users/{id}"));

    }

    @Test
    @Order(5)
    public void testWebHandlerDeleteUsersWithName() throws Throwable {

        Map<String, Object> map = new HashMap<>();
        map.put("httpMethod", "DELETE");
        map.put("resource", "/users/{id}");

        ApiGatewayResponse theAPIGateWayResponse;

        logger.info("Test WebHandlerVerticle responds for DELETE:/users/{id}");
        theAPIGateWayResponse = sl.handleRequest(map, testContext);
        Assert.assertTrue(theAPIGateWayResponse.getBody().contains("Received DELETE:/users/{id}"));

    }

    @Test
    @Order(6)
    public void testWebHandlerUnsupportedResourceURI() throws Throwable {

        Map<String, Object> map = new HashMap<>();
        map.put("httpMethod", "GET");
        map.put("resource", "/invalidresource");

        ApiGatewayResponse theAPIGateWayResponse;

        logger.info("Test WebHandlerVerticle responds with failure");
        theAPIGateWayResponse = sl.handleRequest(map, testContext);
        Assert.assertTrue(theAPIGateWayResponse.getBody().contains("No handlers for address"));

    }


    @AfterAll
    public static void tearDown()
    {
        sl.vertx.undeploy("com.rodellison.serverless.handlers.WebHandlerVerticle");
        sl = null;


    }


}
