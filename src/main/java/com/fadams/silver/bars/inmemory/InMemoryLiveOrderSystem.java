package com.fadams.silver.bars.inmemory;

import com.fadams.silver.bars.OrderRequestValidator;
import com.fadams.silver.bars.model.LiveOrderSystem;
import com.fadams.silver.bars.model.OrderDetails;
import com.fadams.silver.bars.model.OrderNotFoundException;
import com.fadams.silver.bars.model.OrderRequest;
import com.fadams.silver.bars.model.OrdersSummary;
import com.fadams.silver.bars.model.PurchaseType;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class InMemoryLiveOrderSystem implements LiveOrderSystem {
    private final Map<String, OrderDetails> orders = new ConcurrentSkipListMap <>();
    private final SortedMap<BigDecimal, BigDecimal> buyOrdersTotal = new ConcurrentSkipListMap <>(lowestFirst().reversed());
    private final SortedMap<BigDecimal, BigDecimal> sellOrdersTotal = new ConcurrentSkipListMap<>(lowestFirst());

    private final OrderRequestValidator orderRequestValidator;
    private final Supplier<String> nextOrderNumberProvider;
    private final BiFunction<String, OrderRequest, OrderDetails> toOrderDetailsAdapter;

    public InMemoryLiveOrderSystem(OrderRequestValidator orderRequestValidator, Supplier<String> nextOrderNumberProvider, BiFunction<String, OrderRequest, OrderDetails> toOrderDetailsAdapter) {
        this.orderRequestValidator = orderRequestValidator;
        this.nextOrderNumberProvider = nextOrderNumberProvider;
        this.toOrderDetailsAdapter = toOrderDetailsAdapter;
    }

    @Override
    public String registerOrder(OrderRequest orderRequest) {
        // would prefer to return Either<String, RejectionReason> rather than throw validation as an exception
        orderRequestValidator.validate(orderRequest);
        String orderNumber = nextOrderNumberProvider.get();
        // decided to keep the summary in memory so that it is faster to access the summaries
        orders.put(orderNumber, toOrderDetailsAdapter.apply(orderNumber, orderRequest));
        updateOrderTotals(orderRequest);
        return orderNumber;
    }

    @Override
    public OrderDetails orderDetails(String orderId) throws OrderNotFoundException {
        // TODO should validate input is not null + add a test for it
        return Optional.ofNullable(orders.get(orderId)).orElseThrow(notFoundException(orderId));
    }

    @Override
    public OrderDetails cancelOrder(String orderId) throws OrderNotFoundException {
        // TODO should validate input is not null + add a test for it
        OrderDetails orderDetails = Optional.ofNullable(orders.remove(orderId)).orElseThrow(notFoundException(orderId));
        updateOrderTotals(orderDetails.getPricePerKg(), orderDetails.getOrderQuantity().negate(), orderDetails.getPurchaseType());
        return orderDetails;
    }

    @Override
    public OrdersSummary orderSummary(PurchaseType purchaseType) {
        // TODO should validate input is not null + add a test for it
        if (PurchaseType.SELL.equals(purchaseType)) {
            return new OrdersSummary(purchaseType, new TreeMap<>(sellOrdersTotal));
        } else {
            return new OrdersSummary(purchaseType, new TreeMap<>(buyOrdersTotal));
        }
    }

    private Comparator<BigDecimal> lowestFirst() {
        return BigDecimal::compareTo;
    }

    private Supplier<OrderNotFoundException> notFoundException(String orderId) {
        return () -> new OrderNotFoundException(orderId + " not found");
    }

    private void updateOrderTotals(BigDecimal pricePerKgInput, BigDecimal orderQuantity, PurchaseType purchaseType) {
        if (purchaseType.equals(PurchaseType.SELL)) {
            updateOrderTotal(pricePerKgInput, orderQuantity, sellOrdersTotal);
        } else {
            updateOrderTotal(pricePerKgInput, orderQuantity, buyOrdersTotal);
        }
    }

    private void updateOrderTotals(OrderRequest orderRequest) {
        updateOrderTotals(orderRequest.getPricePerKg(), orderRequest.getOrderQuantity(), orderRequest.getPurchaseType());
    }

    private void updateOrderTotal(BigDecimal pricePerKgInput, BigDecimal orderQuantity, Map<BigDecimal, BigDecimal> sellOrdersTotal) {
        sellOrdersTotal.putIfAbsent(pricePerKgInput, BigDecimal.ZERO);
        sellOrdersTotal.computeIfPresent(pricePerKgInput, (pricePerKg, currentTotalInKg) -> currentTotalInKg.add(orderQuantity));
    }

}
