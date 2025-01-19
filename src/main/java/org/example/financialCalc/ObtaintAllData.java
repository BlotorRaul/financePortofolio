package org.example.financialCalc;

import org.example.exportData.TransactionDetail;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@code ObtaintAllData} class provides methods for reading financial data from CSV files
 * and mapping it to structured objects such as transactions, stock charts, S&P 500 entries, and stock prices.
 *
 * <p>The class includes:
 * <ul>
 *     <li>A method for reading transaction details from `executions.csv`</li>
 *     <li>A method for reading stock chart data from `stock_chart_AAPL.csv`</li>
 *     <li>A method for reading S&P 500 data from `sp500.csv`</li>
 *     <li>A method for reading stock price details from `stock_price_AAPL.csv`</li>
 * </ul>
 *
 * <p>The data is parsed into domain-specific objects such as {@code TransactionDetail}, {@code StockChart},
 * {@code SP500Entry}, and {@code StockPrice}.
 */
public class ObtaintAllData {

    /**
     * Reads transaction details from a CSV file and maps them to a list of {@link TransactionDetail} objects.
     *
     * @param csvFile the path to the CSV file containing transaction details
     * @return a {@link List} of {@link TransactionDetail} objects
     */
    public List<TransactionDetail> readTransactions(String csvFile) {
        List<TransactionDetail> transactions = new ArrayList<>();
        String line;
        String csvSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            // Skip the header line
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] data = line.split(csvSplitBy);

                // Parse each field and map to TransactionDetail object
                String execId = data[0];
                String date = data[1];
                String stockSymbol = data[2];
                String transactionType = data[3];
                BigDecimal quantity = new BigDecimal(data[4]);
                BigDecimal pricePerShare = new BigDecimal(data[5]);
                BigDecimal totalAmount = new BigDecimal(data[6]);

                TransactionDetail transaction = new TransactionDetail(
                        execId, date, stockSymbol, transactionType, quantity, pricePerShare, totalAmount
                );
                transactions.add(transaction);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return transactions;
    }

    /**
     * Represents stock chart data for a specific timestamp and closing price.
     */
    public static class StockChart {
        private final LocalDateTime timestamp;
        private final BigDecimal closePrice;

        /**
         * Constructs a {@code StockChart} object with the given timestamp and closing price.
         *
         * @param timestamp the date and time of the stock price
         * @param closePrice the closing price of the stock
         */
        public StockChart(LocalDateTime timestamp, BigDecimal closePrice) {
            this.timestamp = timestamp;
            this.closePrice = closePrice;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public BigDecimal getClosePrice() {
            return closePrice;
        }

        @Override
        public String toString() {
            return "StockChart{" +
                    "timestamp=" + timestamp +
                    ", closePrice=" + closePrice +
                    '}';
        }
    }

    /**
     * Reads stock chart data from a CSV file and maps it to a list of {@link StockChart} objects.
     *
     * @param csvFile the path to the CSV file containing stock chart data
     * @return a {@link List} of {@link StockChart} objects
     */
    public List<StockChart> readStockChart(String csvFile) {
        List<StockChart> stockCharts = new ArrayList<>();
        String line;
        String csvSplitBy = ",";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            // Skip the header line
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] data = line.split(csvSplitBy);

                // Parse timestamp and close price
                LocalDateTime timestamp = LocalDateTime.parse(data[0], formatter);
                BigDecimal closePrice = new BigDecimal(data[1]);

                // Create StockChart object and add to the list
                StockChart stockChart = new StockChart(timestamp, closePrice);
                stockCharts.add(stockChart);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stockCharts;
    }

    /**
     * Represents an entry in the S&P 500 index, including a symbol and regular market price.
     */
    public static class SP500Entry {
        private final String symbol;
        private final BigDecimal regularMarketPrice;

        /**
         * Constructs an {@code SP500Entry} with the given symbol and market price.
         *
         * @param symbol the stock symbol
         * @param regularMarketPrice the regular market price
         */
        public SP500Entry(String symbol, BigDecimal regularMarketPrice) {
            this.symbol = symbol;
            this.regularMarketPrice = regularMarketPrice;
        }

        public String getSymbol() {
            return symbol;
        }

        public BigDecimal getRegularMarketPrice() {
            return regularMarketPrice;
        }

        @Override
        public String toString() {
            return "SP500Entry{" +
                    "symbol='" + symbol + '\'' +
                    ", regularMarketPrice=" + regularMarketPrice +
                    '}';
        }
    }

    /**
     * Reads S&P 500 data from a CSV file and maps it to a list of {@link SP500Entry} objects.
     *
     * @param csvFile the path to the CSV file containing S&P 500 data
     * @return a {@link List} of {@link SP500Entry} objects
     */
    public List<SP500Entry> readSP500(String csvFile) {
        List<SP500Entry> sp500Entries = new ArrayList<>();
        String line;
        String csvSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            // Skip the header line
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] data = line.split(csvSplitBy);

                // Parse symbol and regular market price
                String symbol = data[0];
                BigDecimal regularMarketPrice = new BigDecimal(data[1]);

                // Create SP500Entry object and add to the list
                SP500Entry entry = new SP500Entry(symbol, regularMarketPrice);
                sp500Entries.add(entry);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sp500Entries;
    }
    /**
     * Represents stock price data, including details like symbol, market price, and currency.
     */
    public static class StockPrice {
        private final String symbol;
        private final BigDecimal regularMarketPrice;
        private final BigDecimal previousClose;
        private final BigDecimal dayHigh;
        private final BigDecimal dayLow;
        private final String currency;

        /**
         * Constructs a {@code StockPrice} object with the given details.
         *
         * @param symbol the stock symbol
         * @param regularMarketPrice the regular market price
         * @param previousClose the previous close price
         * @param dayHigh the day's highest price
         * @param dayLow the day's lowest price
         * @param currency the currency of the prices
         */
        public StockPrice(String symbol, BigDecimal regularMarketPrice, BigDecimal previousClose,
                          BigDecimal dayHigh, BigDecimal dayLow, String currency) {
            this.symbol = symbol;
            this.regularMarketPrice = regularMarketPrice;
            this.previousClose = previousClose;
            this.dayHigh = dayHigh;
            this.dayLow = dayLow;
            this.currency = currency;
        }

        public String getSymbol() {
            return symbol;
        }

        public BigDecimal getRegularMarketPrice() {
            return regularMarketPrice;
        }

        public BigDecimal getPreviousClose() {
            return previousClose;
        }

        public BigDecimal getDayHigh() {
            return dayHigh;
        }

        public BigDecimal getDayLow() {
            return dayLow;
        }

        public String getCurrency() {
            return currency;
        }

        @Override
        public String toString() {
            return "StockPrice{" +
                    "symbol='" + symbol + '\'' +
                    ", regularMarketPrice=" + regularMarketPrice +
                    ", previousClose=" + previousClose +
                    ", dayHigh=" + dayHigh +
                    ", dayLow=" + dayLow +
                    ", currency='" + currency + '\'' +
                    '}';
        }
    }

    /**
     * Reads stock price data from a CSV file and maps it to a list of {@link StockPrice} objects.
     *
     * @param csvFile the path to the CSV file containing stock price data
     * @return a {@link List} of {@link StockPrice} objects
     */
    public List<StockPrice> readStockPrice(String csvFile) {
        List<StockPrice> stockPrices = new ArrayList<>();
        String line;
        String csvSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            // Skip the header line
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] data = line.split(csvSplitBy);

                // Parse symbol, regular market price, previous close, day high, day low, and currency
                String symbol = data[0];
                BigDecimal regularMarketPrice = new BigDecimal(data[1]);
                BigDecimal previousClose = new BigDecimal(data[2]);
                BigDecimal dayHigh = new BigDecimal(data[3]);
                BigDecimal dayLow = new BigDecimal(data[4]);
                String currency = data[5];

                // Create StockPrice object and add to the list
                StockPrice stockPrice = new StockPrice(symbol, regularMarketPrice, previousClose, dayHigh, dayLow, currency);
                stockPrices.add(stockPrice);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stockPrices;
    }

}
