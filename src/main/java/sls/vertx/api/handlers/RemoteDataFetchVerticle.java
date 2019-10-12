package sls.vertx.api.handlers;

import sls.vertx.api.Services;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class RemoteDataFetchVerticle extends AbstractVerticle {

    private static final Logger logger = Logger.getLogger(RemoteDataFetchVerticle.class);

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

            JsonObject fetchMessage = JsonObject.mapFrom(message.body());
            String theMessagePathParm = fetchMessage.getValue("path").toString();
            logger.info("RemoteDataHandlerVerticle received request with parm: " + theMessagePathParm);

            executeLongRunningBlockingOperation();

            logger.info("RemoteDataHandlerVerticle processed request");
            final Map<String, Object> response = new HashMap<>();
            response.put("path", theMessagePathParm);
            response.put("body", "...RemoteDataHandler fetched HTML...");
            message.reply(new JsonObject(response));

        });

        startPromise.complete();
    }

}

