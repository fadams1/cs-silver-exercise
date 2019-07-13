package com.fadams.silver.bars.model;

import java.math.BigDecimal;
import java.util.Map;

public class OrdersSummary {
    private final PurchaseType purchaseType;
    private final Map<BigDecimal, BigDecimal> amountsInKgByPrice;

    public OrdersSummary(PurchaseType purchaseType, Map<BigDecimal, BigDecimal> amountsInKgByPrice) {
        this.purchaseType = purchaseType;
        // TODO not properly immutable
        this.amountsInKgByPrice = amountsInKgByPrice;
    }

    public PurchaseType getPurchaseType() {
        return purchaseType;
    }

    public Map<BigDecimal, BigDecimal> getAmountsInKgByPrice() {
        return amountsInKgByPrice;
    }

    @Override
    public String toString() {
        return "OrdersSummary{" +
                "purchaseType=" + purchaseType +
                ", amountsInKgByPrice=" + amountsInKgByPrice +
                '}';
    }
}
