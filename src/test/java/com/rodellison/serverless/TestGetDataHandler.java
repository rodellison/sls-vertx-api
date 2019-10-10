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
public class TestGetDataHandler {

    private final Logger logger = Logger.getLogger(TestGetDataHandler.class);
    private static ServiceLauncher sl;
    private Context testContext;

    @BeforeClass
    public static void setUp() throws IOException {

        //This call establishes service front door, and starts vertx
        sl = new ServiceLauncher();
    }

    @Test
    @Order(1)
    public void testGetRemoteDataHandlerReturnsSuccess2() throws Throwable {

        Map<String, Object> map = new HashMap<>();
        map.put("httpMethod", "GET");
        map.put("resource", "/data");

        ApiGatewayResponse theAPIGateWayResponse1, theAPIGateWayResponse2;

        logger.info("Test RemoteDataHandlerVerticle responds for GET:/data");
        theAPIGateWayResponse1 = sl.handleRequest(map, testContext);
   //     theAPIGateWayResponse2 = sl.handleRequest(map, testContext);
        Assert.assertTrue(theAPIGateWayResponse1.getBody().contains("Received GET:/data"));
   //     Assert.assertTrue(theAPIGateWayResponse2.getBody().contains("Received GET:/data"));
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ie) {        }

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
