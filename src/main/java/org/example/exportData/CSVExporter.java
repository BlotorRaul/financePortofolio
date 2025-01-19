package org.example.exportData;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * The {@code CSVExporter} class provides functionality to export a list of transaction details
 * to a CSV file. Each transaction detail includes information such as execution ID, date, stock symbol,
 * transaction type, quantity, price per share, and total amount.
 *
 * <p>The CSV file will be saved with a predefined header and rows corresponding to each transaction detail.
 */
public class CSVExporter {
    /**
     * Saves a list of transaction details to a CSV file.
     *
     * <p>The CSV file will include the following columns:
     * <ul>
     *     <li>ExecId: The execution ID of the transaction</li>
     *     <li>Date: The date of the transaction</li>
     *     <li>StockSymbol: The stock symbol for the transaction</li>
     *     <li>TransactionType: The type of transaction (e.g., "BUY" or "SELL")</li>
     *     <li>Quantity: The number of shares in the transaction</li>
     *     <li>PricePerShare: The price per share in the transaction</li>
     *     <li>TotalAmount: The total amount for the transaction</li>
     * </ul>
     *
     * <p>The `TotalAmount` is formatted to two decimal places.
     *
     * @param fileName           the name of the CSV file to save
     * @param transactionDetails the list of {@link TransactionDetail} objects to be written to the CSV file
     */
    public static void saveToCSV(String fileName, List<TransactionDetail> transactionDetails) {
        try (FileWriter writer = new FileWriter(fileName)) {
            // Write the header
            writer.append("ExecId,Date,StockSymbol,TransactionType,Quantity,PricePerShare,TotalAmount\n");

            // Write each transaction detail
            for (TransactionDetail detail : transactionDetails) {
                writer.append(detail.getExecId()).append(",")
                        .append(detail.getDate()).append(",")
                        .append(detail.getStockSymbol()).append(",")
                        .append(detail.getTransactionType()).append(",")
                        .append(detail.getQuantity().toString()).append(",")
                        .append(detail.getPricePerShare().toString()).append(",")
                        .append(detail.getTotalAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString())
                        .append("\n");
            }

            System.out.println("Data has been saved to the file: " + fileName);
        } catch (IOException e) {
            System.err.println("Error writing to CSV file: " + e.getMessage());
        }
    }
}
