package com.chintan.handler;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.chintan.entity.ProductCatalog;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.util.*;

public class ProductCatalogPostRequestHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    AmazonDynamoDB client;
    DynamoDBMapper dynamoDBMapper;

    public ProductCatalogPostRequestHandler() {
        client = AmazonDynamoDBClientBuilder.standard().build();
        dynamoDBMapper = new DynamoDBMapper(client);
    }


    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {


        APIGatewayProxyResponseEvent apiGatewayProxyResponseEvent = new APIGatewayProxyResponseEvent();
        try {
            ProductCatalog productCatalog = saveProductCatalog(apiGatewayProxyRequestEvent);
            Map<String, String> responseBody = new HashMap<>();
            String productId = productCatalog.getProductId();
            responseBody.put("responseMessage", "Product " + productCatalog.getProductName() + " created with id: " + productId);
            String responseMessage = new JSONObject(responseBody).toJSONString();
            generateResponse(apiGatewayProxyResponseEvent, responseMessage);
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        return apiGatewayProxyResponseEvent;
    }

    private ProductCatalog saveProductCatalog(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent) throws ParseException, IOException {
        String sellerId = apiGatewayProxyRequestEvent.getPathParameters().get("sellerId");
        String requestString = apiGatewayProxyRequestEvent.getBody();
        JSONParser parser = new JSONParser();
        System.out.println(requestString);
        JSONObject requestJsonObject = (JSONObject) parser.parse(requestString);
        ProductCatalog productCatalog = populateProductCatalog(sellerId, requestJsonObject);
        dynamoDBMapper.save(productCatalog);
        return productCatalog;
    }

    private ProductCatalog populateProductCatalog(String sellerId, JSONObject requestJsonObject) throws IOException {
        ProductCatalog productCatalog = new ProductCatalog();
        productCatalog.setProductType(requestJsonObject.get("productType").toString().toLowerCase());
        productCatalog.setProductId(UUID.randomUUID().toString());
        productCatalog.setSellerId(sellerId);
        productCatalog.setProductName(requestJsonObject.get("productName").toString().toLowerCase());
        productCatalog.setDescription(requestJsonObject.get("description").toString().toLowerCase());
        productCatalog.setSku(requestJsonObject.get("sku").toString().toLowerCase());
        productCatalog.setImageUrl(requestJsonObject.get("imageUrl").toString());
        productCatalog.setPrice(Double.valueOf(requestJsonObject.get("price").toString()));
        System.out.println(requestJsonObject.get("attributes"));
        Map<String, String> attributes =
                new ObjectMapper().readValue(requestJsonObject.get("attributes").toString(), HashMap.class);

        productCatalog.setAttributes(attributes);

        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        formatter.setTimeZone(TimeZone.getTimeZone(ZoneOffset.UTC));
        productCatalog.setCreationTime(formatter.format(new Date()));
        return productCatalog;
    }

    private void generateResponse(APIGatewayProxyResponseEvent apiGatewayProxyResponseEvent, String requestMessage) {
        apiGatewayProxyResponseEvent.setHeaders(Collections.singletonMap("timeStamp", String.valueOf(System.currentTimeMillis())));
        apiGatewayProxyResponseEvent.setStatusCode(200);
        apiGatewayProxyResponseEvent.setBody(requestMessage);
    }
}