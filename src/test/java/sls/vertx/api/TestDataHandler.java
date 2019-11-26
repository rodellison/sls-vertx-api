package sls.vertx.api;

import com.amazonaws.services.lambda.runtime.Context;
import sls.vertx.api.handlers.DataBaseVerticle;
import sls.vertx.api.handlers.DataExtractorVerticle;
import sls.vertx.api.handlers.EventHubVerticle;
import sls.vertx.api.handlers.RemoteDataFetchVerticle;
// Import log4j classes.
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestDataHandler {

    private final Logger logger = LogManager.getLogger(TestDataHandler.class);
    private static ServiceLauncher sl= new ServiceLauncher();;
    private static Context testContext = null;

    {
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
    public void testLoadDataReturnsSuccess() throws Throwable {

        Map<String, Object> map = new HashMap<>();
        map.put("httpMethod", "GET");
        map.put("resource", "/loaddata/{yearmonth}");
        map.put("path", "/loaddata/201910");
        map.put("pathParameters", "{yearmonth=201910}");
        Map<String, Object> map2 = new HashMap<>();
        map2.put("httpMethod", "GET");
        map2.put("resource", "/loaddata/{yearmonth}");
        map2.put("path", "/loaddata/201911");
        map2.put("pathParameters", "{yearmonth=201911}");
        logger.info("Test RemoteDataHandlerVerticle responds for GET:/loaddata/{yearmonth}");

        //running two concurrent requests
        CompletableFuture<ApiGatewayResponse> cf1 = new CompletableFuture<>();
        CompletableFuture<ApiGatewayResponse> cf2 = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            ApiGatewayResponse api1 = sl.handleRequest(map, testContext);
            cf1.complete(api1);
        });
        CompletableFuture.runAsync(() -> {
            ApiGatewayResponse api2 = sl.handleRequest(map2, testContext);
            cf2.complete(api2);
        });
        assertTrue(cf1.get().getBody().contains(map.get("pathParameters").toString()));
        assertTrue(cf2.get().getBody().contains(map2.get("pathParameters").toString()));

    }

    @Test
    @Order(2)
    public void testGetDataReturnsSuccess() throws Throwable {

        Map<String, Object> map = new HashMap<>();
        map.put("httpMethod", "GET");
        map.put("resource", "/data/{yearmonth}");
        map.put("path", "/data/201910");
        map.put("pathParameters", "{yearmonth=201910}");
        Map<String, Object> map2 = new HashMap<>();
        map2.put("httpMethod", "GET");
        map2.put("resource", "/data/{yearmonth}");
        map2.put("path", "/data/201911");
        map2.put("pathParameters", "{yearmonth=201911}");

        logger.info("Test RemoteDataHandlerVerticle responds for GET:/data/{yearmonth}");
        //running two concurrent requests
        CompletableFuture<ApiGatewayResponse> cf1 = new CompletableFuture<>();
        CompletableFuture<ApiGatewayResponse> cf2 = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            ApiGatewayResponse api1 = sl.handleRequest(map, testContext);
            cf1.complete(api1);
        });
        CompletableFuture.runAsync(() -> {
            ApiGatewayResponse api2 = sl.handleRequest(map2, testContext);
            cf2.complete(api2);
        });
        assertTrue(cf1.get().getBody().contains(map.get("pathParameters").toString()));
        assertTrue(cf2.get().getBody().contains(map2.get("pathParameters").toString()));


    }

//    @AfterAll
//    public static void tearDown()
//    {
//        sl.vertx.undeploy(DataBaseVerticle.class.getName());
//        sl.vertx.undeploy(DataExtractorVerticle.class.getName());
//        sl.vertx.undeploy(RemoteDataFetchVerticle.class.getName());
//        sl.vertx.undeploy(EventHubVerticle.class.getName());
//        sl = null;
//    }

}
