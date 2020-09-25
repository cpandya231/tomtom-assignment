package com.chintan.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.*;

@DynamoDBTable(tableName = "ProductCatalog")
public class ProductCatalog {

    @DynamoDBIgnore
    private ProductCompositeKey productCompositeKey;

    @DynamoDBAttribute
    private String productName;


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
}