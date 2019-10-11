package com.rodellison.serverless;

import com.amazonaws.services.lambda.runtime.Context;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestDataHandler {

    private final Logger logger = Logger.getLogger(TestDataHandler.class);
    private static ServiceLauncher sl;
    private Context testContext;

    @BeforeClass
    public static void setUp() throws IOException {

        //This call establishes service front door, and starts vertx
        sl = new ServiceLauncher();
    }

    @Test
    @Order(1)
    public void testLoadDataReturnsSuccess() throws Throwable {

        Map<String, Object> map = new HashMap<>();
        map.put("httpMethod", "GET");
        map.put("resource", "/loaddata/{yearmonth}");
        map.put("pathParameters", "{yearmonth=201910}");

        ApiGatewayResponse theAPIGateWayResponse1, theAPIGateWayResponse2;

        logger.info("Test RemoteDataHandlerVerticle responds for GET:/loaddata/{yearmonth}");
        theAPIGateWayResponse1 = sl.handleRequest(map, testContext);
        Assert.assertTrue(theAPIGateWayResponse1.getBody().contains("Received GET:/loaddata/{yearmonth}"));

    }

    @Test
    @Order(2)
    public void testGetDataReturnsSuccess() throws Throwable {

        Map<String, Object> map = new HashMap<>();
        map.put("httpMethod", "GET");
        map.put("resource", "/data/{yearmonth}");
        map.put("pathParameters", "{yearmonth=201910}");

        ApiGatewayResponse theAPIGateWayResponse1, theAPIGateWayResponse2;

        logger.info("Test RemoteDataHandlerVerticle responds for GET:/data/{yearmonth}");
        theAPIGateWayResponse1 = sl.handleRequest(map, testContext);
        Assert.assertTrue(theAPIGateWayResponse1.getBody().contains("DBHandlerVerticle JSON data"));

    }

    @AfterAll
    public static void tearDown()
    {

        sl.vertx.undeploy("com.rodellison.serverless.handlers.DBHandlerVerticle");
        sl.vertx.undeploy("com.rodellison.serverless.handlers.DataExtratorHandlerVerticle");
        sl.vertx.undeploy("com.rodellison.serverless.handlers.RemoteDataHandlerVerticle");
        sl.vertx.undeploy("com.rodellison.serverless.handlers.EventHandlerVerticle");
        sl = null;

    }


}
