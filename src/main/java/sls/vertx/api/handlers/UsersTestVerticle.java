package sls.vertx.api.handlers;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sls.vertx.api.Services;

import java.util.HashMap;
import java.util.Map;

// Import log4j classes.

public class UsersTestVerticle extends AbstractVerticle {

    private static final Logger logger = LogManager.getLogger(UsersTestVerticle.class);

    public void executeLongRunningBlockingOperation() {
        try {
            logger.info("Executing long running simulation in UsersTestVerticle");
            Thread.sleep(4000);
        } catch (InterruptedException | io.vertx.core.VertxException ie)
        {
          logger.error(ie.getMessage());
        }

    }

    @Override
    public void start(Promise<Void> startPromise) {
        final EventBus eventBus = vertx.eventBus();

        eventBus.consumer(Services.GETUSERS.toString(),message -> {
            // Do something with Vert.x async, reactive APIs

            JsonObject fetchMessage = new JsonObject(message.body().toString());

//            JsonObject fetchMessage = JsonObject.mapFrom(theMessageBody);
            String theMessagePathParm = fetchMessage.getValue("path").toString();
            logger.info("UsersTestVerticle received request with path parm: " + theMessagePathParm);

            executeLongRunningBlockingOperation();

            logger.info("UsersTestVerticle processed request");
            final Map<String, Object> response = new HashMap<>();
            response.put("path", theMessagePathParm);
            response.put("pathParameters", "");
            response.put("body", "...UsersTestVerticle fetched HTML...");
            logger.info("UsersTestVerticle sending GETUSERFINISHED message");
            eventBus.send(Services.GETUSERSFINISHED.toString(), new JsonObject(response));
     //       message.reply(new JsonObject(response));

        });

        startPromise.complete();
    }

}

