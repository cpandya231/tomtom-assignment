package com.chintan.handler;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProductCatalogGetRequestHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

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
        List<ProductCatalog> scanResult = getProductCatalogsUsingScanOperation(mapper, searchQuery);

        List<ProductCatalog> queryResult = getProductCatalogsUsingQueryOperation(mapper, searchQuery);
        return Stream.concat(scanResult.stream(), queryResult.stream()).collect(Collectors.toList());
    }


    private List<ProductCatalog> getProductCatalogsUsingScanOperation(DynamoDBMapper mapper, String searchQuery) {
        Map<String, AttributeValue> scanOperationEAV = new HashMap<>();
        String scanConditionExpression = "contains(productName, :productName)";
        scanOperationEAV.put(":productName", new AttributeValue().withS(searchQuery.toLowerCase()));
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();

        scanExpression.withFilterExpression(scanConditionExpression)
                .withExpressionAttributeValues(scanOperationEAV);
        return mapper.scan(ProductCatalog.class, scanExpression);
    }

    private List<ProductCatalog> getProductCatalogsUsingQueryOperation(DynamoDBMapper mapper, String searchQuery) {
        Map<String, AttributeValue> queryOperationEAV = new HashMap<>();
        String queryConditionExpression = "productType = :productType";
        queryOperationEAV.put(":productType", new AttributeValue().withS(searchQuery.toLowerCase()));
        DynamoDBQueryExpression<ProductCatalog> queryExpression = new DynamoDBQueryExpression<>();
        queryExpression.withKeyConditionExpression(queryConditionExpression).withExpressionAttributeValues(queryOperationEAV);
        List<ProductCatalog> queryResult = mapper.query(ProductCatalog.class, queryExpression);
        return queryResult;
    }


    private void generateResponse(APIGatewayProxyResponseEvent apiGatewayProxyResponseEvent, String requestMessage) {
        apiGatewayProxyResponseEvent.setHeaders(Collections.singletonMap("timeStamp", String.valueOf(System.currentTimeMillis())));
        apiGatewayProxyResponseEvent.setStatusCode(200);
        apiGatewayProxyResponseEvent.setBody(requestMessage);
    }
}