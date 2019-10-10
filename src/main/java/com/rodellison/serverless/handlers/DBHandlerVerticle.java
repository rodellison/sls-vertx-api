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

/**
 * Created by lbulic on 10/19/17.
 */
public class DBHandlerVerticle extends AbstractVerticle {

    private static final Logger logger = Logger.getLogger(DBHandlerVerticle.class);

    public void executeLongRunningBlockingOperation() {
        try {
            logger.info("Executing long running simulation in DBHandlerVerticle");
            Thread.sleep(1000);
        } catch (InterruptedException ie) {

        }

    }

    @Override
    public void start(Promise<Void> startPromise) {
        final EventBus eventBus = vertx.eventBus();

        eventBus.consumer(Services.GETDBDATA.toString(), message -> {
            // Do something with Vert.x async, reactive APIs

            executeLongRunningBlockingOperation();

            final Map<String, Object> response = new HashMap<>();

            response.put("statusCode", 200);
            response.put("body", "DBHandlerVerticle Get DB Data processed request");

            message.reply(new JsonObject(response).encode());
        });

        eventBus.consumer(Services.INSERTDBDATA.toString(), message -> {
            // Do something with Vert.x async, reactive APIs

            logger.info("DBHandler received Insert request");

            // possible long running execution to insert against a remote database
            vertx.executeBlocking(execBlockFuture -> {

                executeLongRunningBlockingOperation();

                execBlockFuture.complete();

            }, res -> {
                logger.info("DBHandler processed Insert request");
            });

         });

        startPromise.complete();
    }

}

