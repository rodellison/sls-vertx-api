package com.rodellison.serverless.handlers;

import com.rodellison.serverless.Services;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class DBHandlerVerticle extends AbstractVerticle {

    private static final Logger logger = Logger.getLogger(DBHandlerVerticle.class);

    public void executeLongRunningBlockingOperation() {
        try {
            logger.info("Executing long running simulation in DBHandlerVerticle");
            Thread.sleep(500);
        } catch (InterruptedException ie) {

        }

    }

    @Override
    public void start(Promise<Void> startPromise) {
        final EventBus eventBus = vertx.eventBus();

        eventBus.consumer(Services.GETDBDATA.toString(), message -> {
            // Do something with Vert.x async, reactive APIs

            JsonObject dbItemsToGet = JsonObject.mapFrom(message.body());
             String theMessagePathParm = dbItemsToGet.getValue("path").toString();

            logger.info("DBHandlerVerticle received Get request: " + dbItemsToGet.getValue("path"));
            executeLongRunningBlockingOperation();
            logger.info("DBHandlerVerticle processed Get request: " + dbItemsToGet.getValue("path"));

            final Map<String, Object> response = new HashMap<>();

            response.put("statusCode", 200);
            response.put("path", theMessagePathParm);
            response.put("body", "...database get completed for: " + theMessagePathParm);

            message.reply(new JsonObject(response).encode());
        });

        eventBus.consumer(Services.INSERTDBDATA.toString(), message -> {
            // Do something with Vert.x async, reactive APIs

            JsonObject dbItemToInsert = JsonObject.mapFrom(message.body());
            String theMessagePathParm = dbItemToInsert.getValue("path").toString();

            logger.info("DBHandlerVerticle received Insert request: " + dbItemToInsert.getValue("path"));
            executeLongRunningBlockingOperation();
            logger.info("DBHandlerVerticle processed Insert request for: " + theMessagePathParm);

            final Map<String, Object> response = new HashMap<>();
            response.put("path", theMessagePathParm);
            response.put("body", "...database insert completed for: " + theMessagePathParm);
            message.reply(new JsonObject(response));

        });

        startPromise.complete();
    }

}

