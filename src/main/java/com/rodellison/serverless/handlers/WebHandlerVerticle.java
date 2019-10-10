package com.rodellison.serverless.handlers;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lbulic on 10/19/17.
 */
public class WebHandlerVerticle extends AbstractVerticle {

    private static final Logger logger = Logger.getLogger(WebHandlerVerticle.class);

    @Override
    public void start(Future<Void> fut) {
        final EventBus eventBus = vertx.eventBus();

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

        fut.complete();
    }
    
}

