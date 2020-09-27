package com.chintan.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import java.util.List;

@DynamoDBTable(tableName = "Cart")
public class Cart {

    @DynamoDBHashKey
    private String userId;

    @DynamoDBAttribute
    private List<ProductInfo> productInfoList;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<ProductInfo> getCartProducts() {
        return productInfoList;
    }

    public void setCartProducts(List<ProductInfo> productInfos) {
        this.productInfoList = productInfos;
    }
}