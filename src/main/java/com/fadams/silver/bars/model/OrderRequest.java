package com.fadams.silver.bars.model;

import java.math.BigDecimal;

/**
 * Not a fan of all the duplication between these two classes #OrderDetails
 */
public class OrderRequest {
    private final String userId;
    private final BigDecimal orderQuantity;
    private final BigDecimal pricePerKg;
    private final PurchaseType purchaseType;

    public OrderRequest(String userId, BigDecimal orderQuantity,
                        BigDecimal pricePerKg, PurchaseType purchaseType) {
        this.userId = userId;
        this.orderQuantity = orderQuantity;
        this.pricePerKg = pricePerKg;
        this.purchaseType = purchaseType;
    }

    public String getUserId() {
        return userId;
    }

    public BigDecimal getOrderQuantity() {
        return orderQuantity;
    }

    public BigDecimal getPricePerKg() {
        return pricePerKg;
    }

    public PurchaseType getPurchaseType() {
        return purchaseType;
    }

    @Override
    public String toString() {
        return "OrderRequest{" +
                "userId='" + userId + '\'' +
                ", orderQuantity=" + orderQuantity +
                ", pricePerKg=" + pricePerKg +
                ", purchaseType=" + purchaseType +
                '}';
    }
}
