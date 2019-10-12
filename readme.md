# Serverless Vertx API

A Java VERT.X based Serverless API Gateway/Lambda template for 
establishing an API Gateway proxy and lambda handler that can:
- Fetch data from a remote web source
- Parse and extract the fetched data
- Insert the extracted data into a database
- Additionally, expose an API that allows for requests to get the data from a database
  
_______________
General Processing Flow
1. ServiceLauncher started, creates verticles for each of the core handlers.
2. ServiceLauncher exposes an _**handleRequest**_ override method, which recieves the incoming
APIGateway request. 
3. The handleRequest starts a CompletableFuture, that encapsulates a send of the incoming
API resource data to the Vertx event message bus. 
4. The _**EventHubVerticle**_ is the main verticle listening for incoming URI event requests, 
using several event.consumer definitions to handle the respective URI request.
5. Various other Verticles are used (some in an execute blocking fashion if they may be long
running - ie. remote data requests, db inserts, etc.)
6. Ultimately, each consumer in the EventHubVerticle creates a reply, which is relayed
back to the ServiceLauncher's handleRequest method.
7. The handleRequest future is marked complete, and an APIGatewayReply is provided
back to the user. 

_______________
**Compile** using:

mvn clean compile

**Package** fat jar using:

mvn package

**Deploy to API Gateway** 

serverless deploy

<hr>

CREDITS:
This app is modeled after a few Github examples:
- https://github.com/serverless/examples/tree/master/aws-java-simple-http-endpoint
- https://github.com/pendula95/aws-java-maven-vertx


