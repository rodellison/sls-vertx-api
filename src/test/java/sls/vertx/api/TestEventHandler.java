package sls.vertx.api;

import com.amazonaws.services.lambda.runtime.Context;
import sls.vertx.api.handlers.DataBaseVerticle;
import sls.vertx.api.handlers.DataExtractorVerticle;
import sls.vertx.api.handlers.EventHubVerticle;
import sls.vertx.api.handlers.RemoteDataFetchVerticle;
import org.junit.Assert;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestEventHandler {

    private final Logger logger = Logger.getLogger(TestEventHandler.class);
    private static ServiceLauncher sl;
    private Context testContext;

    @BeforeClass
    public static void setUp() throws IOException {

         //This call establishes service front door, and starts vertx
        sl = new ServiceLauncher();
        try
        {
            //Just waiting one second for verticles to get up, before running tests
            Thread.sleep(500);

        } catch (InterruptedException ie)
        {

        }
    }

    @Test
    @Order(1)
    public void testEventHandlerGetUsersNoName() throws Throwable {

        Map<String, Object> map = new HashMap<>();
        map.put("httpMethod", "GET");
        map.put("resource", "/users");

        ApiGatewayResponse theAPIGateWayResponse;

        logger.info("Test EventHandlerVerticle responds for GET:/users");
        theAPIGateWayResponse = sl.handleRequest(map, testContext);
        Assert.assertTrue(theAPIGateWayResponse.getBody().contains("Received GET:/users"));

    }

    @Test
    @Order(2)
    public void testEventHandlerGetUsersWithName() throws Throwable {

        Map<String, Object> map = new HashMap<>();
        map.put("httpMethod", "GET");
        map.put("resource", "/users/{id}");

        ApiGatewayResponse theAPIGateWayResponse;

        logger.info("Test EventHandlerVerticle responds for GET:/users/{id}");
        theAPIGateWayResponse = sl.handleRequest(map, testContext);
        Assert.assertTrue(theAPIGateWayResponse.getBody().contains("{id}"));

    }

    @Test
    @Order(3)
    public void testEventHandlerPostUsers() throws Throwable {

        Map<String, Object> map = new HashMap<>();
        map.put("httpMethod", "POST");
        map.put("resource", "/users");

        ApiGatewayResponse theAPIGateWayResponse;

        logger.info("Test EventHandlerVerticle responds for POST:/users");
        theAPIGateWayResponse = sl.handleRequest(map, testContext);
        Assert.assertTrue(theAPIGateWayResponse.getBody().contains("Received POST:/users"));

    }

    @Test
    @Order(4)
    public void testEventHandlerPutUsersWithName() throws Throwable {

        Map<String, Object> map = new HashMap<>();
        map.put("httpMethod", "PUT");
        map.put("resource", "/users/{id}");

        ApiGatewayResponse theAPIGateWayResponse;

        logger.info("Test EventHandlerVerticle responds for Post:/users");
        theAPIGateWayResponse = sl.handleRequest(map, testContext);
        Assert.assertTrue(theAPIGateWayResponse.getBody().contains("Received PUT:/users/{id}"));

    }

    @Test
    @Order(5)
    public void testEventHandlerDeleteUsersWithName() throws Throwable {

        Map<String, Object> map = new HashMap<>();
        map.put("httpMethod", "DELETE");
        map.put("resource", "/users/{id}");

        ApiGatewayResponse theAPIGateWayResponse;

        logger.info("Test EventHandlerVerticle responds for DELETE:/users/{id}");
        theAPIGateWayResponse = sl.handleRequest(map, testContext);
        Assert.assertTrue(theAPIGateWayResponse.getBody().contains("Received DELETE:/users/{id}"));

    }

    @Test
    @Order(6)
    public void testEventHandlerUnsupportedResourceURI() throws Throwable {

        Map<String, Object> map = new HashMap<>();
        map.put("httpMethod", "GET");
        map.put("resource", "/invalidresource");

        ApiGatewayResponse theAPIGateWayResponse;

        logger.info("Test EventHandlerVerticle responds with failure");
        theAPIGateWayResponse = sl.handleRequest(map, testContext);
        Assert.assertTrue(theAPIGateWayResponse.getBody().contains("No handlers for address"));

    }


    @AfterAll
    public static void tearDown()
    {
        sl.vertx.undeploy(DataBaseVerticle.class.getName());
        sl.vertx.undeploy(DataExtractorVerticle.class.getName());
        sl.vertx.undeploy(RemoteDataFetchVerticle.class.getName());
        sl.vertx.undeploy(EventHubVerticle.class.getName());
        sl = null;
    }


}
