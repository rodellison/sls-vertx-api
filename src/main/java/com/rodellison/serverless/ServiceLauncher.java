package com.rodellison.serverless;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class ServiceLauncher implements RequestHandler<Map<String, Object>, ApiGatewayResponse>{

    private static final Logger logger = Logger.getLogger(ServiceLauncher.class);
    public Vertx vertx;
    {
        System.setProperty("vertx.disableFileCPResolving", "true");

        VertxOptions vertxOptions = new VertxOptions()
                .setBlockedThreadCheckInterval(5000);
        vertx = Vertx.vertx(vertxOptions);

        DeploymentOptions standardDeploymentOptions = new DeploymentOptions();
        final int instanceCount = Runtime.getRuntime().availableProcessors();
        logger.info("Starting Service launcher and setting instances to: " + instanceCount);
        standardDeploymentOptions.setInstances(instanceCount);

        final List<String> standardVerticles = Arrays.asList(
                "com.rodellison.serverless.handlers.EventHandlerVerticle",
                "com.rodellison.serverless.handlers.DataExtractorHandlerVerticle",
                "com.rodellison.serverless.handlers.RemoteDataHandlerVerticle",
                "com.rodellison.serverless.handlers.DBHandlerVerticle"
        );

        standardVerticles.stream().forEach(verticle -> vertx.deployVerticle(verticle, standardDeploymentOptions, deployResponse -> {
            if (deployResponse.failed()) {
                logger.error("Unable to deploy verticle: " + verticle,
                        deployResponse.cause());
            } else {
                logger.info(verticle + " deployed");
            }
        }));

    }

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> map, Context context) {

        final CompletableFuture<String> future = new CompletableFuture<String>();

        logger.info(map);
        vertx.eventBus().request(map.get("httpMethod").toString() + ":" + map.get("resource"), new JsonObject(map).encode(), rs -> {
            if(rs.succeeded()) {
                logger.info("ServiceLauncher::handleRequest: SUCCESS");
                logger.info(rs.result().body());
                future.complete(rs.result().body().toString());
            } else {
                logger.info("ServiceLauncher::handleRequest: FAILED");
                logger.info(rs.cause().getMessage());
                future.complete(rs.cause().getMessage());
            }
        });
        try {
            Map<String, String> contentHeader = new HashMap<>();
            contentHeader.put("Content-type", "application/json");

            //different seconds value to account for different values - if calling loaddata, then it could take longer to process
            int seconds = 0;
            seconds = map.get("resource").toString().contains("loaddata") ? 20 : 5;

            return ApiGatewayResponse.builder()
                    .setObjectBody(new Response("Sent by : " + this.toString() + " - " + future.get(seconds,TimeUnit.SECONDS)))
                    .setHeaders(contentHeader)
                    .build();
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
        return ApiGatewayResponse.builder().
                setObjectBody(new Response("TIMEOUT"))
                .build();
    }
}