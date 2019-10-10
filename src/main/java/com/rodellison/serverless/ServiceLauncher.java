package com.rodellison.serverless;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class ServiceLauncher implements RequestHandler<Map<String, Object>, ApiGatewayResponse>{

    private static final Logger logger = Logger.getLogger(ServiceLauncher.class);
    public Vertx vertx;
    {
        System.setProperty("vertx.disableFileCPResolving", "true");
        vertx = Vertx.vertx();
        VertxOptions vertxOptions = new VertxOptions().setClustered(true);
        DeploymentOptions deploymentOptions = new DeploymentOptions();
        deploymentOptions.setInstances(Runtime.getRuntime().availableProcessors());

        final List<String> verticles = Arrays.asList(
                "com.rodellison.serverless.handlers.EventHandlerVerticle");

        verticles.stream().forEach(verticle -> vertx.deployVerticle(verticle, deploymentOptions, deployResponse -> {
            if (deployResponse.failed()) {
                logger.error("Unable to deploy verticle: " + verticle,
                        deployResponse.cause());
            } else {
                logger.debug(verticle + " deployed");
            }
        }));
    }

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> map, Context context) {

        final CompletableFuture<String> future = new CompletableFuture<String>();

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
            return ApiGatewayResponse.builder()
                    .setObjectBody(new Response("Sent by : " + this.toString() + " - " + future.get(5,TimeUnit.SECONDS)))
                    .build();
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
        return ApiGatewayResponse.builder().setObjectBody(new Response("TIMEOUT")).build();
    }
}