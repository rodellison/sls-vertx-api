package com.rodellison.serverless;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.concurrent.*;

public class VertxHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse>{

    private static final Logger LOG = Logger.getLogger(VertxHandler.class);

    private static Vertx vertx;
    {
        System.setProperty("vertx.disableFileCPResolving", "true");
        vertx = Vertx.vertx();
        DeploymentOptions deploymentOptions = new DeploymentOptions().setInstances(Runtime.getRuntime().availableProcessors());
        vertx.deployVerticle(HandlerVerticle.class.getName(), deploymentOptions);
    }


    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> map, Context context) {

        final CompletableFuture<String> future = new CompletableFuture<String>();

        vertx.eventBus().request(map.get("httpMethod").toString() + ":" + map.get("resource"), new JsonObject(map).encode(), rs -> {
            if(rs.succeeded()) {
                LOG.info("SUCCESS");
                LOG.info(rs.result().body());
                future.complete(rs.result().body().toString());
            } else {
                LOG.info("FAILED");
                LOG.info(rs.cause().getMessage());
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