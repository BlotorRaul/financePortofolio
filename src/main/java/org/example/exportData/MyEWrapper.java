package org.example.exportData;

import com.ib.client.*;
import org.example.exportData.EWrapperDefault;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@code MyEWrapper} class extends {@link EWrapperDefault} and provides a customized
 * implementation for handling transaction details retrieved from the Interactive Brokers API.
 *
 * <p>This class processes execution details, calculates total transaction amounts, and stores
 * the transaction details in a list of {@link TransactionDetail} objects.
 *
 * <p>It is specifically designed to work with the Interactive Brokers API and overrides the
 * relevant callback methods for execution details.
 */
class MyEWrapper extends EWrapperDefault {
    private final List<TransactionDetail> transactionDetails = new ArrayList<>();
    /**
     * Processes the details of a single execution received from the Interactive Brokers API.
     *
     * <p>This method is invoked when execution details are received. It:
     * <ul>
     *     <li>Prints the transaction details, including execution ID, date, stock symbol,
     *         transaction type (buy/sell), quantity, price per share, and total amount.</li>
     *     <li>Calculates the total amount of the transaction by multiplying quantity and price per share.</li>
     *     <li>Adds the transaction details to the {@code transactionDetails} list.</li>
     * </ul>
     *
     * @param reqId     the ID of the request that generated this execution detail
     * @param contract  the {@link Contract} object containing details about the stock
     * @param execution the {@link Execution} object containing execution details
     */
    @Override
    public void execDetails(int reqId, Contract contract, Execution execution) {
        System.out.println("=== Tranzacție primită ===");
        System.out.println("ExecId (Transaction ID): " + execution.execId());
        System.out.println("Date: " + execution.time());
        System.out.println("Stock Symbol: " + contract.symbol());
        System.out.println("Transaction Type (BOT/SLD): " + execution.side());
        System.out.println("Quantity: " + execution.shares());
        System.out.println("Price Per Share: " + execution.price());

        // Convert quantity from Decimal to BigDecimal
        BigDecimal quantity = execution.shares().value(); // from Decimal in BigDecimal
        BigDecimal pricePerShare = BigDecimal.valueOf(execution.price()); // from double in BigDecimal

        // Multiply the two BigDecimal values
        BigDecimal totalAmount = quantity.multiply(pricePerShare);

        transactionDetails.add(new TransactionDetail(
                execution.execId(),
                execution.time(),
                contract.symbol(),
                execution.side(),
                quantity,
                pricePerShare,
                totalAmount
        ));

        System.out.println("Total Amount: " + totalAmount.setScale(2, BigDecimal.ROUND_HALF_UP));
        System.out.println("===========================");
    }
    /**
     * Called when all execution details for a specific request ID have been received.
     *
     * <p>This method is invoked by the Interactive Brokers API once all transaction details
     * for the given request ID have been processed.
     *
     * @param reqId the ID of the request for which execution details are complete
     */
    @Override
    public void execDetailsEnd(int reqId) {
        System.out.println("All transactions have been processed for request: " + reqId);
    }

    /**
     * Retrieves the list of all processed transaction details.
     *
     * <p>The list contains {@link TransactionDetail} objects, each representing a single
     * transaction processed by this wrapper.
     *
     * @return a {@link List} of {@link TransactionDetail} objects
     */
    public List<TransactionDetail> getTransactionDetails() {
        return transactionDetails;
    }
}