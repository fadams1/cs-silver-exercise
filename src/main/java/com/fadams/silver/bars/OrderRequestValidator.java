package com.fadams.silver.bars;

import com.fadams.silver.bars.model.OrderRequest;

import java.util.Objects;

public class OrderRequestValidator {
    public void validate(OrderRequest orderRequest) {
        // would not usually use Objects.require
        // would not usually throw validation checks as exceptions as it is not "exceptional".
        //     would prefer to return a validation success or failure object (if Either was available)
        Objects.requireNonNull(orderRequest.getUserId(), "userId is mandatory");
        Objects.requireNonNull(orderRequest.getOrderQuantity(), "orderQuantity is mandatory");
        Objects.requireNonNull(orderRequest.getPricePerKg(), "pricePerKg is mandatory");
        Objects.requireNonNull(orderRequest.getPurchaseType(), "purchaseType is mandatory");
    }
}
