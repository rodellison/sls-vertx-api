package com.rodellison.serverless.handlers;

import com.rodellison.serverless.Services;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class RemoteDataHandlerVerticle extends AbstractVerticle {

    private static final Logger logger = Logger.getLogger(RemoteDataHandlerVerticle.class);

    public void executeLongRunningBlockingOperation() {
        try {
            logger.info("Executing long running simulation in RemoteDataHandlerVerticle");
            Thread.sleep(2000);
        } catch (InterruptedException | io.vertx.core.VertxException ie)
        {
          logger.error(ie.getMessage());
        }

    }

    @Override
    public void start(Promise<Void> startPromise) {
        final EventBus eventBus = vertx.eventBus();

        eventBus.consumer(Services.FETCHWEBDATA.toString(),message -> {
            // Do something with Vert.x async, reactive APIs

            final Message<Object> theMessage = message;
            String theMessagePathParm = (String) theMessage.body();
            logger.info("RemoteDataHandlerVerticle received request with parm: " + theMessagePathParm);

            executeLongRunningBlockingOperation();

            logger.info("RemoteDataHandlerVerticle processed request");

            final Map<String, Object> response = new HashMap<>();
            response.put("body", "...RemoteDataHandler fetched HTML...");
            message.reply(new JsonObject(response));

        });

        startPromise.complete();
    }

}

