package com.chintan.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.*;

@DynamoDBTable(tableName = "User")
public class User {


    @DynamoDBIgnore
    private UserCompositeKey userCompositeKey;

    @DynamoDBAttribute
    private String status;

    @DynamoDBAttribute
    private String createdAt;


    @DynamoDBAttribute
    private ProductInfo productInfo;

    @DynamoDBAttribute
    private String transactionId;


    @DynamoDBHashKey(attributeName = "userId")
    public String getUserId() {
        return null != userCompositeKey ? userCompositeKey.getUserId() : null;
    }

    public void setUserId(String userId) {
        if (null == userCompositeKey) {
            userCompositeKey = new UserCompositeKey();
        }
        userCompositeKey.setUserId(userId);
    }


    @DynamoDBRangeKey(attributeName = "orderId")
    public String getOrderId() {
        return null != userCompositeKey ? userCompositeKey.getOrderId() : null;
    }

    public void setOrderId(String transactionId) {
        if (null == userCompositeKey) {
            userCompositeKey = new UserCompositeKey();
        }
        userCompositeKey.setOrderId(transactionId);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public ProductInfo getProductInfo() {
        return productInfo;
    }

    public void setProductInfo(ProductInfo productInfo) {
        this.productInfo = productInfo;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionId() {
        return transactionId;
    }
}
