package com.rodellison.serverless.handlers;

import com.rodellison.serverless.Services;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class DataExtractorHandlerVerticle extends AbstractVerticle {

    private static final Logger logger = Logger.getLogger(DataExtractorHandlerVerticle.class);


    @Override
    public void start(Promise<Void> startPromise) {
        final EventBus eventBus = vertx.eventBus();

        eventBus.consumer(Services.EXTRACTWEBDATA.toString(), message -> {
            // Do something with Vert.x async, reactive APIs
            JsonObject dataToExtract = JsonObject.mapFrom(message.body());
            logger.info("DataExtracterHandlerVerticle received request: " + dataToExtract.getValue("body"));

            //call function to extract and parse here, should be pretty quick

             logger.info("DataExtracterHandlerVerticle processed request");
            final Map<String, Object> response = new HashMap<>();
            response.put("body", "...a set of JSON items");
            message.reply(new JsonObject(response));

        });

        startPromise.complete();
    }

}

