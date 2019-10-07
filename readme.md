# Florida Keys API

A Java VERT.X serverless application for fetching event data from a Florida Keys web source, then 
formatting and inserting extracted data into a AWS DynamoDB database.
Additionally, via API Gateway, the same serverless app will handle requests for fetching the 
data loaded in the DynamoDB for use. 

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


