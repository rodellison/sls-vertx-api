package sls.vertx.api.handlers;

import sls.vertx.api.Services;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class DataExtractorVerticle extends AbstractVerticle {

    private static final Logger logger = Logger.getLogger(DataExtractorVerticle.class);

    public void executeLongRunningBlockingOperation() {
        try {
            logger.info("Executing long running simulation in DataExtractorHandlerVerticle");
            Thread.sleep(500);
        } catch (InterruptedException ie) {

        }

    }

    @Override
    public void start(Promise<Void> startPromise) {
        final EventBus eventBus = vertx.eventBus();

        eventBus.consumer(Services.EXTRACTWEBDATA.toString(), message -> {
            // Do something with Vert.x async, reactive APIs
            JsonObject dataToExtract = JsonObject.mapFrom(message.body());
            String theMessagePathParm = dataToExtract.getValue("path").toString();
            logger.info("DataExtracterHandlerVerticle received request with parm: " + theMessagePathParm);

            executeLongRunningBlockingOperation();
            // html to extract is in body, e.g. dataToExtract.getValue("body").toString();

            logger.info("DataExtracterHandlerVerticle processed request for: " + theMessagePathParm);
            final Map<String, Object> response = new HashMap<>();
            response.put("path", theMessagePathParm);
            response.put("body", "...a set of JSON items ");
            message.reply(new JsonObject(response));

        });

        startPromise.complete();
    }

}

