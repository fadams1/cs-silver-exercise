package com.fadams.silver.bars;

import com.fadams.silver.bars.model.LiveOrderSystem;
import com.fadams.silver.bars.model.OrderDetails;
import com.fadams.silver.bars.model.OrderNotFoundException;
import com.fadams.silver.bars.model.OrdersSummary;
import com.fadams.silver.bars.model.PurchaseType;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;

import static com.fadams.silver.bars.model.OrderRequestMaker.orderQuantity;
import static com.fadams.silver.bars.model.OrderRequestMaker.orderRequest;
import static com.fadams.silver.bars.model.OrderRequestMaker.pricePerKg;
import static com.fadams.silver.bars.model.OrderRequestMaker.pricePerKilo;
import static com.fadams.silver.bars.model.OrderRequestMaker.purchaseType;
import static com.fadams.silver.bars.model.OrderRequestMaker.quantity;
import static com.fadams.silver.bars.model.OrderRequestMaker.userId;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class LiveOrderSystemTest {
    // would use assertThrows using junit 5
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private final LiverOrderSystemBootstrapper bootstrapper = new LiverOrderSystemBootstrapper();

    @Test
    public void itCanRegisterAndRetrieveAnOrder() throws OrderNotFoundException {
        LiveOrderSystem liveOrderSystem = bootstrapper.bootstrap();
        String inputUserId = "user1";
        BigDecimal inputOrderQuantity = new BigDecimal("3.5");
        BigDecimal inputPricePerKg = new BigDecimal("306");
        PurchaseType inputPurchaseType = PurchaseType.SELL;
        String orderId = liveOrderSystem.registerOrder(orderRequest(
                userId(inputUserId),
                orderQuantity(inputOrderQuantity),
                pricePerKg(inputPricePerKg),
                purchaseType(inputPurchaseType)
        ));

        OrderDetails order = liveOrderSystem.orderDetails(orderId);
        assertThat(order.getOrderId(), is(equalTo(orderId)));
        assertThat(order.getUserId(), is(equalTo(inputUserId)));
        assertThat(order.getOrderQuantity(), is(equalTo(inputOrderQuantity)));
        assertThat(order.getPricePerKg(), is(equalTo(inputPricePerKg)));

    }

    @Test
    public void itThrowsANotFoundExceptionWhenRetrievingAnUnknownOrder() throws OrderNotFoundException {
        LiveOrderSystem liveOrderSystem = bootstrapper.bootstrap();

        String orderId = "unknownOrder";
        thrown.expect(OrderNotFoundException.class);
        thrown.expectMessage(containsString(orderId));

        liveOrderSystem.orderDetails(orderId);
    }

    @Test
    public void itCanCancelARegisteredOrder() throws OrderNotFoundException {
        LiveOrderSystem liveOrderSystem = bootstrapper.bootstrap();
        String inputUserId = "user1";
        BigDecimal inputOrderQuantity = new BigDecimal("3.5");
        BigDecimal inputPricePerKg = new BigDecimal("306");
        PurchaseType inputPurchaseType = PurchaseType.SELL;
        String orderId = liveOrderSystem.registerOrder(orderRequest(
                userId(inputUserId),
                orderQuantity(inputOrderQuantity),
                pricePerKg(inputPricePerKg),
                purchaseType(inputPurchaseType)
        ));

        OrderDetails orderRemoved = liveOrderSystem.cancelOrder(orderId);
        assertThat(orderRemoved.getOrderId(), is(equalTo(orderId)));
        assertThat(orderRemoved.getUserId(), is(equalTo(inputUserId)));
        assertThat(orderRemoved.getOrderQuantity(), is(equalTo(inputOrderQuantity)));
        assertThat(orderRemoved.getPricePerKg(), is(equalTo(inputPricePerKg)));

        thrown.expect(OrderNotFoundException.class);
        thrown.expectMessage(containsString(orderId));

        liveOrderSystem.orderDetails(orderId);
    }

    @Test
    public void itThrowsANotFoundExceptionWhenCancellingAnUnknownOrder() throws OrderNotFoundException {
        LiveOrderSystem liveOrderSystem = bootstrapper.bootstrap();

        String orderId = "unknownOrder";
        thrown.expect(OrderNotFoundException.class);
        thrown.expectMessage(containsString(orderId));

        liveOrderSystem.cancelOrder(orderId);
    }

    @Test
    public void itShowsASummaryOfAllSellOrdersInLowestPriceFirst() {
        LiveOrderSystem liveOrderSystem = bootstrapper.bootstrap();

        liveOrderSystem.registerOrder(orderRequest(
                purchaseType(PurchaseType.SELL),
                quantity("3.5"),
                pricePerKilo("306"))
        );
        liveOrderSystem.registerOrder(orderRequest(
                purchaseType(PurchaseType.SELL),
                quantity("1.2"),
                pricePerKilo("310"))
        );
        liveOrderSystem.registerOrder(orderRequest(
                purchaseType(PurchaseType.SELL),
                quantity("1.5"),
                pricePerKilo("307"))
        );
        liveOrderSystem.registerOrder(orderRequest(
                purchaseType(PurchaseType.SELL),
                quantity("2.0"),
                pricePerKilo("306"))
        );

        OrdersSummary summary = liveOrderSystem.orderSummary(PurchaseType.SELL);
        assertThat(summary.getPurchaseType(), is(equalTo(PurchaseType.SELL)));
        Map<BigDecimal, BigDecimal> amountsInKgByPrice = summary.getAmountsInKgByPrice();

        assertThat(amountsInKgByPrice.size(), is(equalTo(3)));
        Iterator<Map.Entry<BigDecimal, BigDecimal>> amounts = amountsInKgByPrice.entrySet().iterator();

        assertThat(amounts.next().getValue(), is(equalTo(new BigDecimal("5.5"))));
        assertThat(amounts.next().getValue(), is(equalTo(new BigDecimal("1.5"))));
        assertThat(amounts.next().getValue(), is(equalTo(new BigDecimal("1.2"))));
    }

    @Test
    public void itShowsASummaryOfAllBuyOrdersInHighestPriceFirst() {
        LiveOrderSystem liveOrderSystem = bootstrapper.bootstrap();

        liveOrderSystem.registerOrder(orderRequest(
                purchaseType(PurchaseType.BUY),
                quantity("3.5"),
                pricePerKilo("306"))
        );
        liveOrderSystem.registerOrder(orderRequest(
                purchaseType(PurchaseType.BUY),
                quantity("1.2"),
                pricePerKilo("310"))
        );
        liveOrderSystem.registerOrder(orderRequest(
                purchaseType(PurchaseType.BUY),
                quantity("1.5"),
                pricePerKilo("307"))
        );
        liveOrderSystem.registerOrder(orderRequest(
                purchaseType(PurchaseType.BUY),
                quantity("2.0"),
                pricePerKilo("306"))
        );

        OrdersSummary summary = liveOrderSystem.orderSummary(PurchaseType.BUY);
        assertThat(summary.getPurchaseType(), is(equalTo(PurchaseType.BUY)));
        Map<BigDecimal, BigDecimal> amountsInKgByPrice = summary.getAmountsInKgByPrice();

        assertThat(amountsInKgByPrice.size(), is(equalTo(3)));
        Iterator<Map.Entry<BigDecimal, BigDecimal>> amounts = amountsInKgByPrice.entrySet().iterator();

        assertThat(amounts.next().getValue(), is(equalTo(new BigDecimal("1.2"))));
        assertThat(amounts.next().getValue(), is(equalTo(new BigDecimal("1.5"))));
        assertThat(amounts.next().getValue(), is(equalTo(new BigDecimal("5.5"))));
    }

    @Test
    public void itDeductsCancelledBuyOrdersFromTheBuySummary() throws OrderNotFoundException {
        LiveOrderSystem liveOrderSystem = bootstrapper.bootstrap();

        liveOrderSystem.registerOrder(orderRequest(
                purchaseType(PurchaseType.BUY),
                quantity("3.5"),
                pricePerKilo("306"))
        );
        liveOrderSystem.registerOrder(orderRequest(
                purchaseType(PurchaseType.BUY),
                quantity("1.2"),
                pricePerKilo("310"))
        );
        liveOrderSystem.registerOrder(orderRequest(
                purchaseType(PurchaseType.BUY),
                quantity("1.5"),
                pricePerKilo("307"))
        );
        String orderToCancel = liveOrderSystem.registerOrder(orderRequest(
                purchaseType(PurchaseType.BUY),
                quantity("2.0"),
                pricePerKilo("306"))
        );

        OrdersSummary summary = liveOrderSystem.orderSummary(PurchaseType.BUY);
        assertThat(summary.getPurchaseType(), is(equalTo(PurchaseType.BUY)));
        Map<BigDecimal, BigDecimal> amountsInKgByPrice = summary.getAmountsInKgByPrice();

        assertThat(amountsInKgByPrice.size(), is(equalTo(3)));
        Iterator<Map.Entry<BigDecimal, BigDecimal>> amounts = amountsInKgByPrice.entrySet().iterator();

        assertThat(amounts.next().getValue(), is(equalTo(new BigDecimal("1.2"))));
        assertThat(amounts.next().getValue(), is(equalTo(new BigDecimal("1.5"))));
        assertThat(amounts.next().getValue(), is(equalTo(new BigDecimal("5.5"))));

        liveOrderSystem.cancelOrder(orderToCancel);

        OrdersSummary newSummary = liveOrderSystem.orderSummary(PurchaseType.BUY);
        assertThat(newSummary.getPurchaseType(), is(equalTo(PurchaseType.BUY)));
        Map<BigDecimal, BigDecimal> newAmountsInKgByPrice = newSummary.getAmountsInKgByPrice();

        assertThat(newAmountsInKgByPrice.size(), is(equalTo(3)));
        Iterator<Map.Entry<BigDecimal, BigDecimal>> newAmounts = newAmountsInKgByPrice.entrySet().iterator();

        assertThat(newAmounts.next().getValue(), is(equalTo(new BigDecimal("1.2"))));
        assertThat(newAmounts.next().getValue(), is(equalTo(new BigDecimal("1.5"))));
        assertThat(newAmounts.next().getValue(), is(equalTo(new BigDecimal("3.5"))));
    }

    @Test
    public void itDeductsCancelledSellOrdersFromTheSellSummary() throws OrderNotFoundException {
        LiveOrderSystem liveOrderSystem = bootstrapper.bootstrap();

        liveOrderSystem.registerOrder(orderRequest(
                purchaseType(PurchaseType.SELL),
                quantity("3.5"),
                pricePerKilo("306"))
        );
        liveOrderSystem.registerOrder(orderRequest(
                purchaseType(PurchaseType.SELL),
                quantity("1.2"),
                pricePerKilo("310"))
        );
        liveOrderSystem.registerOrder(orderRequest(
                purchaseType(PurchaseType.SELL),
                quantity("1.5"),
                pricePerKilo("307"))
        );
        String orderToCancel = liveOrderSystem.registerOrder(orderRequest(
                purchaseType(PurchaseType.SELL),
                quantity("2.0"),
                pricePerKilo("306"))
        );

        OrdersSummary summary = liveOrderSystem.orderSummary(PurchaseType.SELL);
        assertThat(summary.getPurchaseType(), is(equalTo(PurchaseType.SELL)));
        Map<BigDecimal, BigDecimal> amountsInKgByPrice = summary.getAmountsInKgByPrice();

        assertThat(amountsInKgByPrice.size(), is(equalTo(3)));
        Iterator<Map.Entry<BigDecimal, BigDecimal>> amounts = amountsInKgByPrice.entrySet().iterator();

        assertThat(amounts.next().getValue(), is(equalTo(new BigDecimal("5.5"))));
        assertThat(amounts.next().getValue(), is(equalTo(new BigDecimal("1.5"))));
        assertThat(amounts.next().getValue(), is(equalTo(new BigDecimal("1.2"))));

        liveOrderSystem.cancelOrder(orderToCancel);

        OrdersSummary newSummary = liveOrderSystem.orderSummary(PurchaseType.SELL);
        assertThat(newSummary.getPurchaseType(), is(equalTo(PurchaseType.SELL)));
        Map<BigDecimal, BigDecimal> newAmountsInKgByPrice = newSummary.getAmountsInKgByPrice();

        assertThat(newAmountsInKgByPrice.size(), is(equalTo(3)));
        Iterator<Map.Entry<BigDecimal, BigDecimal>> newAmounts = newAmountsInKgByPrice.entrySet().iterator();

        assertThat(newAmounts.next().getValue(), is(equalTo(new BigDecimal("3.5"))));
        assertThat(newAmounts.next().getValue(), is(equalTo(new BigDecimal("1.5"))));
        assertThat(newAmounts.next().getValue(), is(equalTo(new BigDecimal("1.2"))));
    }

}