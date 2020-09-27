package com.chintan.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;

import java.io.Serializable;

public class UserCompositeKey implements Serializable {
    private static final long serialVersionUID = 1L;
    private String orderId;

    private String userId;

    public UserCompositeKey() {
    }

    public UserCompositeKey(String transactionId, String userId) {
        this.orderId = transactionId;
        this.userId = userId;
    }

    @DynamoDBHashKey
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    @DynamoDBRangeKey
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
