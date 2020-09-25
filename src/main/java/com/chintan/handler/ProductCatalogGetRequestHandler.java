package com.chintan.handler;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.chintan.entity.ProductCatalog;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductCatalogGetRequestHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
        DynamoDBMapper mapper = new DynamoDBMapper(client);
        APIGatewayProxyResponseEvent apiGatewayProxyResponseEvent = new APIGatewayProxyResponseEvent();
        try {
            List<ProductCatalog> productCatalog = getProducts(apiGatewayProxyRequestEvent, mapper);
            ObjectMapper objectMapper = new ObjectMapper();
            generateResponse(apiGatewayProxyResponseEvent, objectMapper.writeValueAsString(productCatalog));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return apiGatewayProxyResponseEvent;
    }

    private List<ProductCatalog> getProducts(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, DynamoDBMapper mapper) {
        String searchQuery = apiGatewayProxyRequestEvent.getQueryStringParameters().get("searchQuery");
        Map<String, AttributeValue> expressionAttributeValuesMap = new HashMap<>();
        String conditionExpression = "contains(productName, :productName)";
        expressionAttributeValuesMap.put(":productName", new AttributeValue().withS(searchQuery));
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();

        scanExpression.withFilterExpression(conditionExpression)
                .withExpressionAttributeValues(expressionAttributeValuesMap);

        return mapper.scan(ProductCatalog.class, scanExpression);

    }

    private void generateResponse(APIGatewayProxyResponseEvent apiGatewayProxyResponseEvent, String requestMessage) {
        apiGatewayProxyResponseEvent.setHeaders(Collections.singletonMap("timeStamp", String.valueOf(System.currentTimeMillis())));
        apiGatewayProxyResponseEvent.setStatusCode(200);
        apiGatewayProxyResponseEvent.setBody(requestMessage);
    }
}