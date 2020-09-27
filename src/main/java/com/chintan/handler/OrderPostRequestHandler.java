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
import com.chintan.entity.ProductInfo;
import com.chintan.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.util.*;

public class OrderPostRequestHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    AmazonDynamoDB client;
    DynamoDBMapper dynamoDBMapper;

    public OrderPostRequestHandler() {
        client = AmazonDynamoDBClientBuilder.standard().build();
        dynamoDBMapper = new DynamoDBMapper(client);
    }


    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {

        APIGatewayProxyResponseEvent apiGatewayProxyResponseEvent = new APIGatewayProxyResponseEvent();
        try {
            User user = saveOrderDetails(apiGatewayProxyRequestEvent);
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("responseMessage", "Order created for user  " + user.getUserId() + ". OrderId: " + user.getOrderId());
            String responseMessage = new JSONObject(responseBody).toJSONString();
            generateResponse(apiGatewayProxyResponseEvent, responseMessage);
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        return apiGatewayProxyResponseEvent;
    }

    private User saveOrderDetails(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent) throws ParseException, IOException {
        String userId = apiGatewayProxyRequestEvent.getPathParameters().get("userId");

        String requestString = apiGatewayProxyRequestEvent.getBody();
        JSONParser parser = new JSONParser();
        System.out.println(requestString);
        JSONObject requestJsonObject = (JSONObject) parser.parse(requestString);
        ProductInfo requestedProductInfo = new ObjectMapper().readValue(requestJsonObject.get("productInfo").toString(), ProductInfo.class);
        User user = new User();
        user.setUserId(userId);
        user.setOrderId(UUID.randomUUID().toString());
        user.setTransactionId(requestJsonObject.get("transactionId").toString());
        user.setStatus(requestJsonObject.get("status").toString());
        user.setProductInfo(requestedProductInfo);
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        formatter.setTimeZone(TimeZone.getTimeZone(ZoneOffset.UTC));
        user.setCreatedAt(formatter.format(new Date()));

        dynamoDBMapper.save(user);
        return user;
    }

    private Optional<Cart> getCartInfo(String userId) {
        Map<String, AttributeValue> queryOperationEAV = new HashMap<>();
        String queryConditionExpression = "userId = :userId";
        queryOperationEAV.put(":userId", new AttributeValue().withS(userId.toLowerCase()));
        DynamoDBQueryExpression<Cart> queryExpression = new DynamoDBQueryExpression<>();
        queryExpression.withKeyConditionExpression(queryConditionExpression).withExpressionAttributeValues(queryOperationEAV);

        return dynamoDBMapper.query(Cart.class, queryExpression).stream().findFirst();
    }


    private Cart populateCart(String userId, List<ProductInfo> requestedProductInfos) {
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setCartProducts(requestedProductInfos);
        return cart;
    }

    private void generateResponse(APIGatewayProxyResponseEvent apiGatewayProxyResponseEvent, String requestMessage) {
        apiGatewayProxyResponseEvent.setHeaders(Collections.singletonMap("timeStamp", String.valueOf(System.currentTimeMillis())));
        apiGatewayProxyResponseEvent.setStatusCode(200);
        apiGatewayProxyResponseEvent.setBody(requestMessage);
    }
}