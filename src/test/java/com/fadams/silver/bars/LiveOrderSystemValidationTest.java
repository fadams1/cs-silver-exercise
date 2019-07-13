package com.fadams.silver.bars;

import com.fadams.silver.bars.model.LiveOrderSystem;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static com.fadams.silver.bars.model.OrderRequestMaker.orderQuantity;
import static com.fadams.silver.bars.model.OrderRequestMaker.orderRequest;
import static com.fadams.silver.bars.model.OrderRequestMaker.pricePerKg;
import static com.fadams.silver.bars.model.OrderRequestMaker.purchaseType;
import static com.fadams.silver.bars.model.OrderRequestMaker.userId;
import static org.hamcrest.CoreMatchers.containsString;

public class LiveOrderSystemValidationTest {
    // would use assertThrows using junit 5
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private final LiverOrderSystemBootstrapper bootstrapper = new LiverOrderSystemBootstrapper();

    @Test
    public void itThrowsAValidationExceptionWhenRequestHasNoUser() {
        LiveOrderSystem liveOrderSystem = bootstrapper.bootstrap();

        thrown.expect(NullPointerException.class);
        thrown.expectMessage(containsString("userId"));

        liveOrderSystem.registerOrder(orderRequest(userId(null)));
    }

    @Test
    public void itThrowsAValidationExceptionWhenRequestHasNoOrderQuantity() {
        LiveOrderSystem liveOrderSystem = bootstrapper.bootstrap();

        thrown.expect(NullPointerException.class);
        thrown.expectMessage(containsString("orderQuantity"));

        liveOrderSystem.registerOrder(orderRequest(orderQuantity(null)));
    }

    @Test
    public void itThrowsAValidationExceptionWhenRequestHasNoPricePerKg() {
        LiveOrderSystem liveOrderSystem = bootstrapper.bootstrap();

        thrown.expect(NullPointerException.class);
        thrown.expectMessage(containsString("pricePerKg"));

        liveOrderSystem.registerOrder(orderRequest(pricePerKg(null)));
    }

    @Test
    public void itThrowsAValidationExceptionWhenRequestHasNoPurchaseType() {
        LiveOrderSystem liveOrderSystem = bootstrapper.bootstrap();

        thrown.expect(NullPointerException.class);
        thrown.expectMessage(containsString("purchaseType"));

        liveOrderSystem.registerOrder(orderRequest(purchaseType(null)));
    }

}