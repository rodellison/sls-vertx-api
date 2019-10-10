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

/**
 * Created by lbulic on 10/19/17.
 */
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

            executeLongRunningBlockingOperation();

            //From here, after obtaining the Data, we'd send the html over to the DataExtractHandler for processing
            Map<String, Object> theDataToExtract = new HashMap<>();
            theDataToExtract.put("html_item_body", "the html item body content");
            eventBus.send(Services.EXTRACTWEBDATA.toString(), new JsonObject(theDataToExtract));  //fire forget as this service will eventually send on to DBHandlerVerticle


            final Map<String, Object> response = new HashMap<>();
            response.put("body", "RemoteDataHandlerVerticle processed request");
            message.reply(new JsonObject(response).encode());
        });

        startPromise.complete();
    }

}

