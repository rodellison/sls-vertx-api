package com.rodellison.serverless.handlers;

import com.rodellison.serverless.Services;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lbulic on 10/19/17.
 */
public class DataExtractorHandlerVerticle extends AbstractVerticle {

    private static final Logger logger = Logger.getLogger(DataExtractorHandlerVerticle.class);


    @Override
    public void start(Promise<Void> startPromise) {
        final EventBus eventBus = vertx.eventBus();

        eventBus.consumer(Services.EXTRACTWEBDATA.toString(), message -> {
            // Do something with Vert.x async, reactive APIs

            logger.info("DataExtracterHandler received request");
            JsonObject theMessageObject = (JsonObject)message.body();

            //From here, after obtaining the Data, we'd send the html over to the DataExtractHandler for processing
            Map<String, Object> theDataToExtract = new HashMap<>();
            theDataToExtract.put("html_item_body", "the html item body content");
            eventBus.send(Services.INSERTDBDATA.toString(), new JsonObject(theDataToExtract));  //fire forget as this service will eventually send on to DBHandlerVerticle

            logger.info("DataExtracterHandler processed request");

//            final Map<String, Object> response = new HashMap<>();
//            response.put("body", "DataExtracterHandler processed request");
//            message.reply(new JsonObject(response).encode());

        });

        startPromise.complete();
    }

}

