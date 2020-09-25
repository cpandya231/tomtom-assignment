package com.chintan.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;

import java.io.Serializable;

public class ProductCompositeKey implements Serializable {
    private static final long serialVersionUID = 1L;
    private String productType;

    private String productId;

    public ProductCompositeKey() {
    }

    public ProductCompositeKey(String productType, String productId) {
        this.productType = productType;
        this.productType = productId;
    }

    @DynamoDBHashKey
    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    @DynamoDBRangeKey
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
