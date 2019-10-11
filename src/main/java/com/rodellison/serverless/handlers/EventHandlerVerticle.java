package com.rodellison.serverless.handlers;

import com.rodellison.serverless.Services;
import io.vertx.core.*;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


public class EventHandlerVerticle extends AbstractVerticle {

    private static final Logger logger = Logger.getLogger(EventHandlerVerticle.class);

    @Override
    public void start(Promise<Void> startPromise) {
        final EventBus eventBus = vertx.eventBus();

        eventBus.consumer("GET:/loaddata/{yearmonth}", message -> {

            String theMessage = message.body().toString();
            JsonObject messageJson = new JsonObject(theMessage);
            String theMessagePathParm = messageJson.getValue("pathParameters").toString();

            logger.info("GET:/loaddata/{yearmonth} function invoked with parm: " + theMessagePathParm);

            //Calling out to an external web page to get data could take time, trying executeBlocking here
            vertx.<String>executeBlocking(execBlockFuture -> {
                final CompletableFuture<String> remoteDataHandlerFuture = new CompletableFuture<String>();

                eventBus.request(Services.FETCHWEBDATA.toString(), theMessagePathParm, rs -> {
                    if (rs.succeeded()) {
                        logger.info("RemoteDataHandler: SUCCESS");
                        remoteDataHandlerFuture.complete(rs.result().body().toString());
                    } else {
                        logger.info("RemoteDataHandler: FAILED");
                        remoteDataHandlerFuture.complete(rs.cause().getMessage());
                    }
                });

                String RemoteDataHandlerResult = "";
                try {
                    RemoteDataHandlerResult = remoteDataHandlerFuture.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    RemoteDataHandlerResult = e.getMessage();
                }

                execBlockFuture.complete(RemoteDataHandlerResult);

            }, resFetch -> {

                logger.info(resFetch.result());
                JsonObject fetchResult = new JsonObject(resFetch.result());

                //Calling out to an external web page to get data could take time, trying executeBlocking here
                vertx.<String>executeBlocking(execBlockFuture -> {
                    final CompletableFuture<String> dataExtractHandlerFuture = new CompletableFuture<String>();

                    eventBus.request(Services.EXTRACTWEBDATA.toString(), fetchResult, rs -> {
                        if (rs.succeeded()) {
                            logger.info("DataExtractHandler: SUCCESS");
                            dataExtractHandlerFuture.complete(rs.result().body().toString());
                        } else {
                            logger.info("DataExtractHandler: FAILED");
                            dataExtractHandlerFuture.complete(rs.cause().getMessage());
                        }
                    });

                    String DataExtractHandlerResult = "";
                    try {
                        DataExtractHandlerResult = dataExtractHandlerFuture.get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                        DataExtractHandlerResult = e.getMessage();
                    }

                    execBlockFuture.complete(DataExtractHandlerResult);

                }, resExtract -> {

                    logger.info(resExtract.result());
                    JsonObject extractResult = new JsonObject(resExtract.result());
                    //Calling out to an external web page to get data could take time, trying executeBlocking here
                    vertx.<String>executeBlocking(execBlockFuture -> {
                        final CompletableFuture<String> dbHandlerFuture = new CompletableFuture<String>();

                        eventBus.request(Services.INSERTDBDATA.toString(), extractResult, rs -> {
                            if (rs.succeeded()) {
                                logger.info("DataExtractHandler: SUCCESS");
                                dbHandlerFuture.complete(rs.result().body().toString());
                            } else {
                                logger.info("DataExtractHandler: FAILED");
                                dbHandlerFuture.complete(rs.cause().getMessage());
                            }
                        });

                        String dbHandlerResult = "";
                        try {
                            dbHandlerResult = dbHandlerFuture.get();
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                            dbHandlerResult = e.getMessage();
                        }

                        execBlockFuture.complete(dbHandlerResult);

                    }, resDB -> {

                        final Map<String, Object> response = new HashMap<>();
                        response.put("statusCode", 200);
                        response.put("body", "Received GET:/loaddata/{yearmonth}, result: " + resDB.result());
                        message.reply(new JsonObject(response).encode());
                    });
                });

            });

        });

        eventBus.consumer("GET:/data/{yearmonth}", message -> {

            final Message<Object> theMessage = message;
            String theMessagePathParm = (String) theMessage.body();

            logger.info("GET:/data/{yearmonth} function invoked with parm: " + theMessagePathParm);

            //Calling out to an external web page to get data could take time, trying executeBlocking here
            vertx.<String>executeBlocking(execBlockFuture -> {
                final CompletableFuture<String> remoteDataHandlerFuture = new CompletableFuture<String>();

                eventBus.request(Services.GETDBDATA.toString(), theMessagePathParm, rs -> {
                    if (rs.succeeded()) {
                        logger.info("DBHandler: SUCCESS");
                        remoteDataHandlerFuture.complete(rs.result().body().toString());
                    } else {
                        logger.info("DBHandler: FAILED");
                        remoteDataHandlerFuture.complete(rs.cause().getMessage());
                    }
                });

                String DBHandlerResult = "";
                try {
                    DBHandlerResult = remoteDataHandlerFuture.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    DBHandlerResult = e.getMessage();
                }

                execBlockFuture.complete(DBHandlerResult);

            }, res -> {

                final Map<String, Object> response = new HashMap<>();
                response.put("statusCode", 200);
                response.put("body", "Received GET:/data/{yearmonth}, result: " + res.result());
                message.reply(new JsonObject(response).encode());

            });
        });

        eventBus.consumer("GET:/users", message -> {
            // Do something with Vert.x async, reactive APIs
            final Map<String, Object> response = new HashMap<>();
            response.put("statusCode", 200);
            response.put("body", "Received GET:/users");
            message.reply(new JsonObject(response).encode());
        });

        eventBus.consumer("GET:/users/{id}", message -> {
            // Do something with Vert.x async, reactive APIs
            final Map<String, Object> response = new HashMap<>();
            response.put("statusCode", 200);
            response.put("body", "Received GET:/users/{id}");
            message.reply(new JsonObject(response).encode());
        });

        eventBus.consumer("POST:/users", message -> {
            // Do something with Vert.x async, reactive APIs
            final Map<String, Object> response = new HashMap<>();
            response.put("statusCode", 201);
            response.put("body", "Received POST:/users");
            message.reply(new JsonObject(response).encode());
        });

        eventBus.consumer("PUT:/users/{id}", message -> {
            // Do something with Vert.x async, reactive APIs
            final Map<String, Object> response = new HashMap<>();
            response.put("statusCode", 200);
            response.put("body", "Received PUT:/users/{id}");
            message.reply(new JsonObject(response).encode());
        });

        eventBus.consumer("DELETE:/users/{id}", message -> {
            // Do something with Vert.x async, reactive APIs
            final Map<String, Object> response = new HashMap<>();
            response.put("statusCode", 200);
            response.put("body", "Received DELETE:/users/{id}");
            message.reply(new JsonObject(response).encode());
        });

        startPromise.complete();
    }

}

