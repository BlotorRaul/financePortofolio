package org.example.exportData;

import java.math.BigDecimal;

/**
 * The {@code TransactionDetail} class represents the details of a single stock transaction.
 *
 * <p>This class encapsulates information about a transaction, including:
 * <ul>
 *   <li>Execution ID</li>
 *   <li>Date</li>
 *   <li>Stock symbol</li>
 *   <li>Transaction type (e.g., "BUY" or "SELL")</li>
 *   <li>Quantity of shares</li>
 *   <li>Price per share</li>
 *   <li>Total transaction amount</li>
 * </ul>
 *
 * <p>It provides getter methods to access each of these attributes.
 */
public class TransactionDetail {
    private final String execId;
    private final String date;
    private final String stockSymbol;
    private final String transactionType;
    private final BigDecimal quantity;
    private final BigDecimal pricePerShare;
    private final BigDecimal totalAmount;

    public TransactionDetail(String execId, String date, String stockSymbol, String transactionType,
                             BigDecimal quantity, BigDecimal pricePerShare, BigDecimal totalAmount) {
        this.execId = execId;
        this.date = date;
        this.stockSymbol = stockSymbol;
        this.transactionType = transactionType;
        this.quantity = quantity;
        this.pricePerShare = pricePerShare;
        this.totalAmount = totalAmount;
    }

    public String getExecId() {
        return execId;
    }

    public String getDate() {
        return date;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getPricePerShare() {
        return pricePerShare;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
}