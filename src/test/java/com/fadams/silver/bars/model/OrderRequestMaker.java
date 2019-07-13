package com.fadams.silver.bars.model;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyValue;

import java.math.BigDecimal;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.MakeItEasy.with;
import static com.natpryce.makeiteasy.Property.newProperty;

/**
 * Would usually put this in an api test package to make test construct code reusable to any client of the library
 */
public class OrderRequestMaker {

    private static final Property<OrderRequest, String> USER_ID = newProperty();
    private static final Property<OrderRequest, BigDecimal> ORDER_QUANTITY = newProperty();
    private static final Property<OrderRequest, BigDecimal> PRICE_PER_KG = newProperty();
    private static final Property<OrderRequest, PurchaseType> PURCHASE_TYPE = newProperty();

    private static final Instantiator<OrderRequest> ORDER_REQUEST = propertyLookup -> new OrderRequest(
            propertyLookup.valueOf(USER_ID, "user1"),
            propertyLookup.valueOf(ORDER_QUANTITY, BigDecimal.ONE),
            propertyLookup.valueOf(PRICE_PER_KG, BigDecimal.TEN),
            propertyLookup.valueOf(PURCHASE_TYPE, PurchaseType.BUY)
    );

    @SafeVarargs
    public static OrderRequest orderRequest(PropertyValue<? super OrderRequest, ?>... properties) {
        return make(a(ORDER_REQUEST, properties));
    }


    public static PropertyValue<OrderRequest, BigDecimal> orderQuantity(BigDecimal inputOrderQuantity) {
        return with(ORDER_QUANTITY, inputOrderQuantity);
    }

    public static PropertyValue<OrderRequest, BigDecimal> quantity(String inputOrderQuantity) {
        return orderQuantity(new BigDecimal(inputOrderQuantity));
    }

    public static PropertyValue<OrderRequest, String> userId(String inputUserId) {
        return with(USER_ID, inputUserId);
    }

    public static PropertyValue<OrderRequest, PurchaseType> purchaseType(PurchaseType inputPurchaseType) {
        return with(PURCHASE_TYPE, inputPurchaseType);
    }

    public static PropertyValue<OrderRequest, BigDecimal> pricePerKg(BigDecimal inputPricePerKg) {
        return with(PRICE_PER_KG, inputPricePerKg);
    }

    public static PropertyValue<OrderRequest, BigDecimal> pricePerKilo(String inputPricePerKg) {
        return pricePerKg(new BigDecimal(inputPricePerKg));
    }

}