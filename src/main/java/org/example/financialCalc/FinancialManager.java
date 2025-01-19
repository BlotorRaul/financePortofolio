package org.example.financialCalc;

import org.example.exportData.TransactionDetail;

import javax.swing.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The {@code FinancialManager} class provides functionality to manage and analyze financial data,
 * including transactions, stock charts, S&P 500 data, and stock prices. It also computes key metrics
 * such as ROI, cumulative ROI, volatility, and the Sharpe Ratio.
 *
 * <p>This class reads data from CSV files, processes it, and provides methods to generate data
 * for a financial dashboard. It also includes utility methods for printing raw data and metrics.
 */
public class FinancialManager {
    private final List<TransactionDetail> transactions;
    private final List<ObtaintAllData.StockChart> stockCharts;
    private final List<ObtaintAllData.SP500Entry> sp500Entries;
    private final List<ObtaintAllData.StockPrice> stockPrices;

    /**
     * Constructs a {@code FinancialManager} instance by reading data from the specified CSV files.
     *
     * @param dataManager         an instance of {@link ObtaintAllData} for reading data
     * @param transactionsCsvFile the path to the CSV file containing transaction data
     * @param stockChartCsvFile   the path to the CSV file containing stock chart data
     * @param sp500CsvFile        the path to the CSV file containing S&P 500 data
     * @param stockPriceCsvFile   the path to the CSV file containing stock price data
     */
    public FinancialManager(ObtaintAllData dataManager, String transactionsCsvFile, String stockChartCsvFile,
                            String sp500CsvFile, String stockPriceCsvFile) {
        this.transactions = dataManager.readTransactions(transactionsCsvFile);
        this.stockCharts = dataManager.readStockChart(stockChartCsvFile);
        this.sp500Entries = dataManager.readSP500(sp500CsvFile);
        this.stockPrices = dataManager.readStockPrice(stockPriceCsvFile);
    }

    /**
     * Calculates the ROI for a "BOT" (buy) transaction.
     *
     * @param transaction         the {@link TransactionDetail} of the transaction
     * @param regularMarketPrice  the current market price of the stock
     * @return the ROI as a percentage
     */
    public double calculateROIBot(TransactionDetail transaction, double regularMarketPrice) {
        return FinancialMetricsCalculator.calculateROI(
                regularMarketPrice,
                transaction.getPricePerShare().doubleValue(),
                transaction.getTotalAmount().doubleValue(),
                transaction.getQuantity().doubleValue()
        );
    }

    /**
     * Calculates the ROI for a "SELL" transaction.
     *
     * @param sellPrice   the selling price
     * @param buyPrice    the buying price
     * @param quantity    the quantity of shares sold
     * @param totalAmount the total transaction amount
     * @return the ROI as a percentage
     */
    public double calculateROISell(double sellPrice, double buyPrice, double quantity, double totalAmount) {
        return FinancialMetricsCalculator.calculateROI(
                sellPrice,
                buyPrice,
                totalAmount,
                quantity
        );
    }

    /**
     * Calculates the volatility of stock prices.
     *
     * @return the volatility as a percentage
     */
    public double calculateVolatility() {
        List<Double> prices = stockCharts.stream()
                .map(chart -> chart.getClosePrice().doubleValue())
                .collect(Collectors.toList());
        return FinancialMetricsCalculator.calculateVolatility(prices);
    }

    /**
     * Calculates the Sharpe Ratio based on ROI and volatility.
     *
     * @param roi        the average ROI
     * @param volatility the volatility of stock prices
     * @return the Sharpe Ratio
     */
    public double calculateSharpeRatio(double roi, double volatility) {
        return FinancialMetricsCalculator.calculateSharpeRatio(roi, volatility);
    }

    /**
     * Retrieves a map containing data for the financial dashboard.
     *
     * <p>The map contains the following keys:
     * <ul>
     *   <li>"dates": A list of transaction dates</li>
     *   <li>"roiValues": A list of ROI values for each transaction</li>
     *   <li>"cumulativeROI": A list of cumulative ROI values</li>
     *   <li>"sp500Values": A list of S&P 500 market prices</li>
     *   <li>"sharpeRatio": The calculated Sharpe Ratio</li>
     * </ul>
     *
     * @return a {@link Map} with the dashboard data
     */
    public Map<String, Object> getDashboardData() {
        Map<String, Object> dashboardData = new HashMap<>();

        // Data for ROI
        List<String> dates = transactions.stream()
                .map(TransactionDetail::getDate)
                .collect(Collectors.toList());

        List<Double> roiValues = transactions.stream()
                .filter(transaction -> transaction.getTransactionType().equals("BOT"))
                .map(transaction -> {
                    double regularMarketPrice = stockPrices.stream()
                            .filter(price -> price.getSymbol().equals(transaction.getStockSymbol()))
                            .findFirst()
                            .map(price -> price.getRegularMarketPrice().doubleValue())
                            .orElse(0.0);
                    return calculateROIBot(transaction, regularMarketPrice);
                })
                .collect(Collectors.toList());

        // ROI cumulativ
        List<Double> cumulativeROI = new ArrayList<>();
        double cumulativeSum = 0;
        for (double roi : roiValues) {
            cumulativeSum += roi;
            cumulativeROI.add(cumulativeSum);
        }

        // Data for S&P500
        List<Double> sp500Values = sp500Entries.stream()
                .map(entry -> entry.getRegularMarketPrice().doubleValue())
                .collect(Collectors.toList());

        // Volatility and Sharpe Ratio
        double volatility = calculateVolatility();
        double averageROI = roiValues.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double sharpeRatio = calculateSharpeRatio(averageROI, volatility);

        // add in map
        dashboardData.put("dates", dates);
        dashboardData.put("roiValues", roiValues);
        dashboardData.put("cumulativeROI", cumulativeROI);
        dashboardData.put("sp500Values", sp500Values);
        dashboardData.put("sharpeRatio", sharpeRatio);

        return dashboardData;
    }

    public void printMetrics() {
        System.out.println("=== Calculating Metrics ===");

        // ROI pentru tranzacțiile de tip BOT
        transactions.stream()
                .filter(transaction -> transaction.getTransactionType().equals("BOT"))
                .forEach(transaction -> {
                    double regularMarketPrice = stockPrices.stream()
                            .filter(price -> price.getSymbol().equals(transaction.getStockSymbol()))
                            .findFirst()
                            .map(price -> price.getRegularMarketPrice().doubleValue())
                            .orElse(0.0);
                    double roiBot = calculateROIBot(transaction, regularMarketPrice);
                    System.out.printf("ROI (BOT) for transaction %s: %.2f%%%n", transaction.getExecId(), roiBot);
                });

        // volatility
        double volatility = calculateVolatility();
        System.out.printf("Volatility: %.4f%n", volatility);

        // Sharpe Ratio
        double averageROI = transactions.stream()
                .filter(transaction -> transaction.getTransactionType().equals("BOT"))
                .mapToDouble(transaction -> {
                    double regularMarketPrice = stockPrices.stream()
                            .filter(price -> price.getSymbol().equals(transaction.getStockSymbol()))
                            .findFirst()
                            .map(price -> price.getRegularMarketPrice().doubleValue())
                            .orElse(0.0);
                    return calculateROIBot(transaction, regularMarketPrice);
                }).average().orElse(0.0);

        double sharpeRatio = calculateSharpeRatio(averageROI, volatility);
        System.out.printf("Sharpe Ratio: %.4f%n", sharpeRatio);
    }

    public void printTransactions() {
        System.out.println("=== Transactions ===");
        transactions.forEach(transaction -> System.out.println(
                "ExecId: " + transaction.getExecId() +
                        ", Date: " + transaction.getDate() +
                        ", StockSymbol: " + transaction.getStockSymbol() +
                        ", TransactionType: " + transaction.getTransactionType() +
                        ", Quantity: " + transaction.getQuantity() +
                        ", PricePerShare: " + transaction.getPricePerShare() +
                        ", TotalAmount: " + transaction.getTotalAmount()
        ));
    }

    public void printStockCharts() {
        System.out.println("=== Stock Charts ===");
        stockCharts.forEach(System.out::println);
    }

    public void printSP500Entries() {
        System.out.println("=== SP500 Entries ===");
        sp500Entries.forEach(System.out::println);
    }

    public void printStockPrices() {
        System.out.println("=== Stock Prices ===");
        stockPrices.forEach(System.out::println);
    }

    public List<TransactionDetail> getTransactions() {
        return transactions;
    }

    public List<ObtaintAllData.StockChart> getStockCharts() {
        return stockCharts;
    }

    public List<ObtaintAllData.SP500Entry> getSp500Entries() {
        return sp500Entries;
    }

    public List<ObtaintAllData.StockPrice> getStockPrices() {
        return stockPrices;
    }

    public static void main(String[] args) {
        ObtaintAllData dataManager = new ObtaintAllData();

        String transactionsCsvFile = "executions.csv";
        String stockChartCsvFile = "stock_chart_AAPL.csv";
        String sp500CsvFile = "sp500.csv";
        String stockPriceCsvFile = "stock_price_AAPL.csv";

        FinancialManager financialManager = new FinancialManager(dataManager, transactionsCsvFile, stockChartCsvFile,
                sp500CsvFile, stockPriceCsvFile);

        financialManager.printMetrics();

        Map<String, Object> dashboardData = financialManager.getDashboardData();

        SwingUtilities.invokeLater(() -> {
            FinancialDashboard dashboard = new FinancialDashboard(dashboardData);
            dashboard.setVisible(true);
        });
    }
}
