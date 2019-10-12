package com.rodellison.serverless;

import com.rodellison.serverless.handlers.*;
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
import java.util.concurrent.CompletableFuture;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestDataHandler {

    private final Logger logger = Logger.getLogger(TestDataHandler.class);
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
    public void testLoadDataReturnsSuccess() throws Throwable {

        Map<String, Object> map = new HashMap<>();
        map.put("httpMethod", "GET");
        map.put("resource", "/loaddata/{yearmonth}");
        map.put("path", "/loaddata/201910");
        Map<String, Object> map2 = new HashMap<>();
        map2.put("httpMethod", "GET");
        map2.put("resource", "/loaddata/{yearmonth}");
        map2.put("path", "/loaddata/201911");

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
        Assert.assertTrue(cf1.get().getBody().contains(map.get("path").toString()));
        Assert.assertTrue(cf2.get().getBody().contains(map2.get("path").toString()));

    }

    @Test
    @Order(2)
    public void testGetDataReturnsSuccess() throws Throwable {

        Map<String, Object> map = new HashMap<>();
        map.put("httpMethod", "GET");
        map.put("resource", "/data/{yearmonth}");
        map.put("path", "/data/201910");
        Map<String, Object> map2 = new HashMap<>();
        map2.put("httpMethod", "GET");
        map2.put("resource", "/data/{yearmonth}");
        map2.put("path", "/data/201911");

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
        Assert.assertTrue(cf1.get().getBody().contains(map.get("path").toString()));
        Assert.assertTrue(cf2.get().getBody().contains(map2.get("path").toString()));


    }

    @AfterAll
    public static void tearDown()
    {
        sl.vertx.undeploy(DBHandlerVerticle.class.getName());
        sl.vertx.undeploy(DataExtractorHandlerVerticle.class.getName());
        sl.vertx.undeploy(RemoteDataHandlerVerticle.class.getName());
        sl.vertx.undeploy(EventHandlerVerticle.class.getName());
        sl = null;
    }

}
