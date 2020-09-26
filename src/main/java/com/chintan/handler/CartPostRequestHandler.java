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
import com.chintan.entity.CartProduct;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CartPostRequestHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    AmazonDynamoDB client;
    DynamoDBMapper dynamoDBMapper;

    public CartPostRequestHandler() {
        client = AmazonDynamoDBClientBuilder.standard().build();
        dynamoDBMapper = new DynamoDBMapper(client);
    }


    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {

        APIGatewayProxyResponseEvent apiGatewayProxyResponseEvent = new APIGatewayProxyResponseEvent();
        try {
            Cart cart = saveItemsToUserCart(apiGatewayProxyRequestEvent);
            Map<String, String> responseBody = new HashMap<>();
            String userId = cart.getUserId();
            responseBody.put("responseMessage", "Products added to " + userId + "'s cart ");
            String responseMessage = new JSONObject(responseBody).toJSONString();
            generateResponse(apiGatewayProxyResponseEvent, responseMessage);
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        return apiGatewayProxyResponseEvent;
    }

    private Cart saveItemsToUserCart(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent) throws ParseException, IOException {
        String userId = apiGatewayProxyRequestEvent.getPathParameters().get("userId");

        String requestString = apiGatewayProxyRequestEvent.getBody();
        JSONParser parser = new JSONParser();
        System.out.println(requestString);
        JSONObject requestJsonObject = (JSONObject) parser.parse(requestString);
        List<CartProduct> requestedCartProducts = Arrays.asList(new ObjectMapper().readValue(requestJsonObject.get("products").toString(), CartProduct[].class));
        Optional<Cart> cartOptional = getCartInfo(userId);
        Cart cart;
        if (cartOptional.isPresent()) {
            cart = cartOptional.get();
            List<CartProduct> existingCartProducts = cart.getCartProducts();
            cart.setCartProducts(Stream.concat(existingCartProducts.stream()
                    , requestedCartProducts.stream()).collect(Collectors.toList()));
        } else {
            cart = populateCart(userId, requestedCartProducts);
        }

        dynamoDBMapper.save(cart);
        return cart;
    }

    private Optional<Cart> getCartInfo(String userId) {
        Map<String, AttributeValue> queryOperationEAV = new HashMap<>();
        String queryConditionExpression = "userId = :userId";
        queryOperationEAV.put(":userId", new AttributeValue().withS(userId.toLowerCase()));
        DynamoDBQueryExpression<Cart> queryExpression = new DynamoDBQueryExpression<>();
        queryExpression.withKeyConditionExpression(queryConditionExpression).withExpressionAttributeValues(queryOperationEAV);

        return dynamoDBMapper.query(Cart.class, queryExpression).stream().findFirst();
    }


    private Cart populateCart(String userId, List<CartProduct> requestedCartProducts) {
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setCartProducts(requestedCartProducts);
        return cart;
    }

    private void generateResponse(APIGatewayProxyResponseEvent apiGatewayProxyResponseEvent, String requestMessage) {
        apiGatewayProxyResponseEvent.setHeaders(Collections.singletonMap("timeStamp", String.valueOf(System.currentTimeMillis())));
        apiGatewayProxyResponseEvent.setStatusCode(200);
        apiGatewayProxyResponseEvent.setBody(requestMessage);
    }
}