package com.fadams.silver.bars;

import com.fadams.silver.bars.inmemory.InMemoryLiveOrderSystem;
import com.fadams.silver.bars.model.LiveOrderSystem;
import com.fadams.silver.bars.model.OrderDetails;
import com.fadams.silver.bars.model.OrderRequest;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class LiverOrderSystemBootstrapper {

    // would usually put these behind services themselves or classes at the very least
    private final AtomicInteger SEQUENCE_NUMBER = new AtomicInteger(0);
    private final Supplier<String> nextOrderNumberProvider = () -> "" + SEQUENCE_NUMBER.getAndIncrement();

    private final BiFunction<String, OrderRequest, OrderDetails> toOrderDetailsAdapter = (orderId, orderRequest) ->
            new OrderDetails(orderId,
            orderRequest.getUserId(), orderRequest.getOrderQuantity(),
            orderRequest.getPricePerKg(), orderRequest.getPurchaseType());

    private OrderRequestValidator orderRequestValidator = new OrderRequestValidator();

    public LiveOrderSystem bootstrap() {
        // not returning a singleton at the moment, boostrapper could be changed to initialise only once if required later
        return new InMemoryLiveOrderSystem(orderRequestValidator, nextOrderNumberProvider,
                toOrderDetailsAdapter);
    }
}
