package com.chintan.handler;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.chintan.entity.Cart;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CartGetRequestHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {


    AmazonDynamoDB client;
    DynamoDBMapper dynamoDBMapper;

    public CartGetRequestHandler() {
        client = AmazonDynamoDBClientBuilder.standard().build();
        dynamoDBMapper = new DynamoDBMapper(client);
    }


    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {
        APIGatewayProxyResponseEvent apiGatewayProxyResponseEvent = new APIGatewayProxyResponseEvent();
        Optional<Cart> cartOptional = getCart(apiGatewayProxyRequestEvent);
        generateResponse(apiGatewayProxyResponseEvent, cartOptional);
        return apiGatewayProxyResponseEvent;
    }

    private Optional<Cart> getCart(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent) {

        String searchQuery = apiGatewayProxyRequestEvent.getQueryStringParameters().get("userId");

        return getCartInfo(searchQuery);
    }


    private Optional<Cart> getCartInfo(String userId) {
        Map<String, AttributeValue> queryOperationEAV = new HashMap<>();
        String queryConditionExpression = "userId = :userId";
        queryOperationEAV.put(":userId", new AttributeValue().withS(userId.toLowerCase()));
        DynamoDBQueryExpression<Cart> queryExpression = new DynamoDBQueryExpression<>();
        queryExpression.withKeyConditionExpression(queryConditionExpression).withExpressionAttributeValues(queryOperationEAV);

        return dynamoDBMapper.query(Cart.class, queryExpression).stream().findFirst();
    }

    private void generateResponse(APIGatewayProxyResponseEvent apiGatewayProxyResponseEvent, Optional<Cart> cartOptional) {
        apiGatewayProxyResponseEvent.setHeaders(Collections.singletonMap("timeStamp", String.valueOf(System.currentTimeMillis())));
        apiGatewayProxyResponseEvent.setStatusCode(200);
        String responseString = null;
        try {
            responseString = cartOptional.isPresent() ? new ObjectMapper().writeValueAsString(cartOptional.get()) : "";
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        apiGatewayProxyResponseEvent.setBody(responseString);
    }
}