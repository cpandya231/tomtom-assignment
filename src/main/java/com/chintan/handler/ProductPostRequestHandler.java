package com.chintan.handler;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.chintan.entity.ProductCatalog;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProductPostRequestHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {


        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();

        DynamoDBMapper mapper = new DynamoDBMapper(client);

        APIGatewayProxyResponseEvent apiGatewayProxyResponseEvent = new APIGatewayProxyResponseEvent();
        try {
            String requestString = apiGatewayProxyRequestEvent.getBody();
            JSONParser parser = new JSONParser();
            System.out.println(requestString);
            System.out.println("Parser is null? " + (null == parser));
            JSONObject requestJsonObject = (JSONObject) parser.parse(requestString);
            String requestMessage = null;
            String responseMessage = null;
            if (requestJsonObject != null) {
                if (requestJsonObject.get("requestMessage") != null) {
                    requestMessage = requestJsonObject.get("requestMessage").toString();
                }
            }

            ProductCatalog productCatalog= new ProductCatalog();
            productCatalog.setProductType(requestJsonObject.get("productType").toString());
            productCatalog.setProductId(UUID.randomUUID().toString());
            productCatalog.setProductName(requestJsonObject.get("productName").toString());

            mapper.save(productCatalog);

            Map<String, String> responseBody = new HashMap<String, String>();
            responseBody.put("responseMessage", requestMessage);
            responseMessage = new JSONObject(responseBody).toJSONString();
            generateResponse(apiGatewayProxyResponseEvent, responseMessage);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return apiGatewayProxyResponseEvent;
    }

    private void generateResponse(APIGatewayProxyResponseEvent apiGatewayProxyResponseEvent, String requestMessage) {
        apiGatewayProxyResponseEvent.setHeaders(Collections.singletonMap("timeStamp", String.valueOf(System.currentTimeMillis())));
        apiGatewayProxyResponseEvent.setStatusCode(200);
        apiGatewayProxyResponseEvent.setBody(requestMessage);
    }
}