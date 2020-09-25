package com.chintan.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.*;

import java.util.Map;

@DynamoDBTable(tableName = "ProductCatalog")
public class ProductCatalog {

    @DynamoDBIgnore
    private ProductCompositeKey productCompositeKey;

    @DynamoDBAttribute
    private String productName;

    @DynamoDBAttribute
    private Double price;

    @DynamoDBAttribute
    private String imageUrl;

    @DynamoDBAttribute
    private String description;

    @DynamoDBAttribute
    private String sku;

    @DynamoDBAttribute
    private String sellerId;

    @DynamoDBAttribute
    private Map<String, String> attributes;

    @DynamoDBAttribute
    private String creationTime;


    @DynamoDBHashKey(attributeName = "productType")
    public String getProductType() {
        return null != productCompositeKey ? productCompositeKey.getProductType() : null;
    }

    public void setProductType(String productType) {
        if (null == productCompositeKey) {
            productCompositeKey = new ProductCompositeKey();
        }
        productCompositeKey.setProductType(productType);
    }


    @DynamoDBRangeKey(attributeName = "productId")
    public String getProductId() {
        return null != productCompositeKey ? productCompositeKey.getProductId() : null;
    }

    public void setProductId(String productId) {
        if (null == productCompositeKey) {
            productCompositeKey = new ProductCompositeKey();
        }
        productCompositeKey.setProductId(productId);
    }

    public ProductCompositeKey getProductCompositeKey() {
        return productCompositeKey;
    }

    public void setProductCompositeKey(ProductCompositeKey productCompositeKey) {
        this.productCompositeKey = productCompositeKey;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public String getCreationTime() {
        return creationTime;
    }
}