package sls.vertx.api.handlers;

import sls.vertx.api.Services;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
// Import log4j classes.
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.HashMap;
import java.util.Map;

public class DataBaseVerticle extends AbstractVerticle {
    private static final Logger logger = LogManager.getLogger(DataBaseVerticle.class);
 //   private static final Logger logger = Logger.getLogger(DataBaseVerticle.class);

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
             String theMessagePathParm = dbItemsToGet.getValue("pathParameters").toString();

            logger.info("DBHandlerVerticle received Get request: " + dbItemsToGet.getValue("pathParameters"));
            executeLongRunningBlockingOperation();
            logger.info("DBHandlerVerticle processed Get request: " + dbItemsToGet.getValue("pathParameters"));

            final Map<String, Object> response = new HashMap<>();

            response.put("statusCode", 200);
            response.put("pathParameters", theMessagePathParm);
            response.put("body", "...database get completed for: " + theMessagePathParm);

            message.reply(new JsonObject(response).encode());
        });

        eventBus.consumer(Services.INSERTDBDATA.toString(), message -> {
            // Do something with Vert.x async, reactive APIs

            JsonObject dbItemToInsert = JsonObject.mapFrom(message.body());
            String theMessagePathParm = dbItemToInsert.getValue("pathParameters").toString();

            logger.info("DBHandlerVerticle received Insert request: " + dbItemToInsert.getValue("pathParameters"));
            executeLongRunningBlockingOperation();
            logger.info("DBHandlerVerticle processed Insert request for: " + theMessagePathParm);

            final Map<String, Object> response = new HashMap<>();
            response.put("pathParameters", theMessagePathParm);
            response.put("body", "...database insert completed for: " + theMessagePathParm);
            message.reply(new JsonObject(response));

        });

        startPromise.complete();
    }

}

