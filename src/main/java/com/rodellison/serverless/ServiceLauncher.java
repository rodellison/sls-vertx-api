package com.rodellison.serverless;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.rodellison.serverless.handlers.DBHandlerVerticle;
import com.rodellison.serverless.handlers.DataExtractorHandlerVerticle;
import com.rodellison.serverless.handlers.EventHandlerVerticle;
import com.rodellison.serverless.handlers.RemoteDataHandlerVerticle;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.*;

public class ServiceLauncher implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private static final Logger logger = Logger.getLogger(ServiceLauncher.class);
    public Vertx vertx;

    {
        VertxOptions vertxOptions = new VertxOptions()
                .setBlockedThreadCheckInterval(5000);
        vertx = Vertx.vertx(vertxOptions);

        final int instanceCount = Runtime.getRuntime().availableProcessors();
        logger.info("Starting Service launcher and setting instances to: " + instanceCount);

        DeploymentOptions standardDeploymentOptions = new DeploymentOptions()
                .setInstances(instanceCount);

        DeploymentOptions workerDeploymentOptions = new DeploymentOptions()
                .setWorker(true);

        CompletableFuture.allOf(

                deploy(RemoteDataHandlerVerticle.class.getName(), standardDeploymentOptions),
                deploy(DBHandlerVerticle.class.getName(), standardDeploymentOptions),
                deploy(DataExtractorHandlerVerticle.class.getName(), standardDeploymentOptions),
                deploy(EventHandlerVerticle.class.getName(), standardDeploymentOptions)

        ).whenComplete((nada, thrown) -> {
            logger.info("Deploy verticles complete.");
        });
    }

    private CompletableFuture<Boolean> deploy(String name, DeploymentOptions opts) {
        CompletableFuture<Boolean> cf = new CompletableFuture<Boolean>();

        CompletableFuture.supplyAsync(() -> {
            vertx.deployVerticle(name, opts, res -> {
                if (res.failed()) {
                    logger.error("Failed to deploy verticle " + name);
                    cf.complete(false);
                } else {
                    logger.info("Deployed verticle " + name);
                    cf.complete(true);
                }
            });
            return null;
        });
        return cf;
    }

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> map, Context context) {

        final CompletableFuture<String> future = new CompletableFuture<String>();
        logger.info(map);
        vertx.eventBus().request(map.get("httpMethod").toString() + ":" + map.get("resource"), new JsonObject(map).encode(), rs -> {
            if (rs.succeeded()) {
                logger.info("ServiceLauncher::handleRequest: SUCCESS");
                logger.debug(rs.result().body());
                future.complete(rs.result().body().toString());
            } else {
                logger.info("ServiceLauncher::handleRequest: FAILED");
                logger.error(rs.cause().getMessage());
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
                    .setObjectBody(new Response("Sent by : " + this.toString() + " - " + future.get(seconds, TimeUnit.SECONDS)))
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