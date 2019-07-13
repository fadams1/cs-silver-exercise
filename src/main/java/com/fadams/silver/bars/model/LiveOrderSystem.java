package com.fadams.silver.bars.model;

public interface LiveOrderSystem {

    /**
     *
     * @param orderRequest incoming order
     * @return order id to be used for finding order information later and tracking
     */
    String registerOrder(OrderRequest orderRequest);

    /**
     *
     * @param orderId order to find
     * @return found order details
     * @throws OrderNotFoundException when order not found
     */
    OrderDetails orderDetails(String orderId) throws OrderNotFoundException;

    /**
     *
     * @param orderId order to find
     * @return removed order details
     * @throws OrderNotFoundException when order not found
     */
    OrderDetails cancelOrder(String orderId) throws OrderNotFoundException;

    OrdersSummary orderSummary(PurchaseType purchaseType);
}
