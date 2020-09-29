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
import com.chintan.entity.OrderDetailsDTO;
import com.chintan.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OrderGetRequestHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    AmazonDynamoDB client;
    DynamoDBMapper dynamoDBMapper;

    public OrderGetRequestHandler() {
        client = AmazonDynamoDBClientBuilder.standard().build();
        dynamoDBMapper = new DynamoDBMapper(client);
    }

    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {

        APIGatewayProxyResponseEvent apiGatewayProxyResponseEvent = new APIGatewayProxyResponseEvent();
        try {
            List<OrderDetailsDTO> orders = getOrders(apiGatewayProxyRequestEvent);
            ObjectMapper objectMapper = new ObjectMapper();
            generateResponse(apiGatewayProxyResponseEvent, objectMapper.writeValueAsString(orders));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return apiGatewayProxyResponseEvent;
    }

    private List<OrderDetailsDTO> getOrders(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent) {

        String userId = apiGatewayProxyRequestEvent.getPathParameters().get("userId");

        List<User> queryResult = getOrdersUsingQueryOperation(userId);
        return queryResult.stream().map(this::populateOrderDetailsDTO).collect(Collectors.toList());
    }

    private OrderDetailsDTO populateOrderDetailsDTO(User result) {
        OrderDetailsDTO orderDetailsDTO = new OrderDetailsDTO();
        orderDetailsDTO.setOrderId(result.getOrderId());
        orderDetailsDTO.setStatus(result.getStatus());
        orderDetailsDTO.setCreatedAt(result.getCreatedAt());
        orderDetailsDTO.setImageUrl(result.getProductInfo().getImageUrl());
        orderDetailsDTO.setProductId(result.getProductInfo().getProductId());
        orderDetailsDTO.setProductName(result.getProductInfo().getProductName());
        orderDetailsDTO.setProductType(result.getProductInfo().getProductType());
        orderDetailsDTO.setQuantity(result.getProductInfo().getQuantity());
        return orderDetailsDTO;
    }


    private List<User> getOrdersUsingQueryOperation(String userId) {
        Map<String, AttributeValue> queryOperationEAV = new HashMap<>();
        String queryConditionExpression = "userId = :userId";
        queryOperationEAV.put(":userId", new AttributeValue().withS(userId.toLowerCase()));
        DynamoDBQueryExpression<User> queryExpression = new DynamoDBQueryExpression<>();
        queryExpression.withKeyConditionExpression(queryConditionExpression).withExpressionAttributeValues(queryOperationEAV);
        List<User> queryResult = dynamoDBMapper.query(User.class, queryExpression);
        return queryResult;
    }


    private void generateResponse(APIGatewayProxyResponseEvent apiGatewayProxyResponseEvent, String requestMessage) {
        apiGatewayProxyResponseEvent.setHeaders(Collections.singletonMap("timeStamp", String.valueOf(System.currentTimeMillis())));
        apiGatewayProxyResponseEvent.setStatusCode(200);
        apiGatewayProxyResponseEvent.setBody(requestMessage);
    }
}