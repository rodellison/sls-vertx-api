package com.rodellison.serverless.handlers;

import com.rodellison.serverless.ApiGatewayResponse;
import com.rodellison.serverless.Response;
import com.rodellison.serverless.Services;
import io.vertx.core.*;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by lbulic on 10/19/17.
 */
public class EventHandlerVerticle extends AbstractVerticle {

    private static final Logger logger = Logger.getLogger(EventHandlerVerticle.class);

    @Override
    public void start(Promise<Void> startPromise) {
        final EventBus eventBus = vertx.eventBus();

        eventBus.consumer("GET:/data", message -> {

            logger.info("GET:/data function invoked");

            //Calling out to an external web page to get data could take time, trying executeBlocking here
            vertx.<String>executeBlocking(execBlockFuture -> {
                JsonObject jsObj = new JsonObject();
                jsObj.put("message", "remotedatahandler");
                final CompletableFuture<String> remoteDataHandlerFuture = new CompletableFuture<String>();

                eventBus.request(Services.FETCHWEBDATA.toString(),jsObj.encode(), rs -> {
                    if(rs.succeeded()) {
                        logger.info("RemoteDataHandler: SUCCESS");
                        remoteDataHandlerFuture.complete(rs.result().body().toString());
                    } else {
                        logger.info("RemoteDataHandler: FAILED");
                        remoteDataHandlerFuture.complete(rs.cause().getMessage());
                    }
                });

                String RemoteDataHandlerResult = "";
                try {
                    RemoteDataHandlerResult = remoteDataHandlerFuture.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    RemoteDataHandlerResult = e.getMessage();
                 }

                execBlockFuture.complete(RemoteDataHandlerResult);

            }, res -> {
                    logger.info("GET:/data function handled");
                    final Map<String, Object> response = new HashMap<>();
                    response.put("statusCode", 200);
                    response.put("body", "Received GET:/data, result: " + res.result());
                    message.reply(new JsonObject(response).encode());
            });
        });

        eventBus.consumer("GET:/users", message -> {
            // Do something with Vert.x async, reactive APIs
            final Map<String, Object> response = new HashMap<>();
            response.put("statusCode", 200);
            response.put("body", "Received GET:/users");
            message.reply(new JsonObject(response).encode());
        });

        eventBus.consumer("GET:/users/{id}", message -> {
            // Do something with Vert.x async, reactive APIs
            final Map<String, Object> response = new HashMap<>();
            response.put("statusCode", 200);
            response.put("body", "Received GET:/users/{id}");
            message.reply(new JsonObject(response).encode());
        });

        eventBus.consumer("POST:/users", message -> {
            // Do something with Vert.x async, reactive APIs
            final Map<String, Object> response = new HashMap<>();
            response.put("statusCode", 201);
            response.put("body", "Received POST:/users");
            message.reply(new JsonObject(response).encode());
        });

        eventBus.consumer("PUT:/users/{id}", message -> {
            // Do something with Vert.x async, reactive APIs
            final Map<String, Object> response = new HashMap<>();
            response.put("statusCode", 200);
            response.put("body", "Received PUT:/users/{id}");
            message.reply(new JsonObject(response).encode());
        });

        eventBus.consumer("DELETE:/users/{id}", message -> {
            // Do something with Vert.x async, reactive APIs
            final Map<String, Object> response = new HashMap<>();
            response.put("statusCode", 200);
            response.put("body", "Received DELETE:/users/{id}");
            message.reply(new JsonObject(response).encode());
        });

        startPromise.complete();
    }

}

