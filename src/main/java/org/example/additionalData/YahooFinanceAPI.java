package org.example.additionalData;

import okhttp3.*;
import org.example.Config;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * The {@code YahooFinanceAPI} class provides methods to interact with the Yahoo Finance API
 * and fetch financial data such as stock prices, charts, and S&P500 data.
 *
 * <p>This class includes methods for:
 * <ul>
 *   <li>Retrieving historical stock chart data</li>
 *   <li>Fetching real-time stock price details</li>
 *   <li>Fetching the current data for S&P 500 index components</li>
 * </ul>
 *
 * <p>The results are saved as CSV files for further processing or analysis.
 *
 * <p><strong>Note:</strong> The class uses the {@code OkHttpClient} library for HTTP requests
 * and assumes that the Yahoo Finance API key and host details are correctly configured.
 */
public class YahooFinanceAPI {
    private static final String API_KEY = Config.getProperty("api_key");
    private static final String HOST = Config.getProperty("host");

    /**
     * Fetches historical stock chart data for the given stock symbol and saves it to a CSV file.
     *
     * <p>The data includes:
     * <ul>
     *   <li>Timestamps</li>
     *   <li>Closing prices</li>
     * </ul>
     *
     * <p>The CSV file is saved in the format `stock_chart_{symbol}.csv`.
     *
     * @param stockSymbol the stock symbol to fetch the chart data for (e.g., "AAPL")
     * @throws Exception if there is an error in the API call or file writing process
     */
    public static void getStockChart(String stockSymbol) throws Exception {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://" + HOST + "/api/stock/get-chart?region=US&range=1d&symbol=" + stockSymbol + "&interval=5m")
                .get()
                .addHeader("x-rapidapi-key", API_KEY)
                .addHeader("x-rapidapi-host", HOST)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                JSONObject json = new JSONObject(responseBody);

                JSONArray timestamps = json.getJSONObject("chart")
                        .getJSONArray("result")
                        .getJSONObject(0)
                        .getJSONArray("timestamp");

                JSONArray closes = json.getJSONObject("chart")
                        .getJSONArray("result")
                        .getJSONObject(0)
                        .getJSONObject("indicators")
                        .getJSONArray("quote")
                        .getJSONObject(0)
                        .getJSONArray("close");

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        .withZone(ZoneId.of("America/New_York"));

                try (FileWriter writer = new FileWriter("stock_chart_" + stockSymbol + ".csv")) {
                    writer.append("Timestamp,ClosePrice\n");

                    for (int i = 0; i < timestamps.length(); i++) {
                        long timestamp = timestamps.getLong(i);
                        double closePrice = closes.optDouble(i, Double.NaN);

                        if (!Double.isNaN(closePrice)) {
                            String formattedTimestamp = formatter.format(Instant.ofEpochSecond(timestamp));
                            writer.append(formattedTimestamp).append(",")
                                    .append(String.valueOf(closePrice)).append("\n");
                        }
                    }
                }

                System.out.println("The data for the symbol " + stockSymbol + " has been saved in stock_chart_" + stockSymbol + ".csv");
            } else {
                System.err.println("API error for symbol " + stockSymbol + ": " + response.message());
            }
        }
    }

    /**
     * Fetches the data for the S&P 500 index components and saves it to a CSV file.
     *
     * <p>The data includes:
     * <ul>
     *   <li>Stock symbols</li>
     *   <li>Regular market prices</li>
     * </ul>
     *
     * <p>The CSV file is saved as `sp500.csv`.
     *
     * @throws Exception if there is an error in the API call or file writing process
     */
    public static void getSP500() throws Exception {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://" + HOST + "/api/market/get-sp-500?quote_type=ETFS&region=US&count=30&offset=0&language=en-US")
                .get()
                .addHeader("x-rapidapi-key", API_KEY)
                .addHeader("x-rapidapi-host", HOST)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                JSONObject json = new JSONObject(responseBody);

                JSONArray quotes = json.getJSONObject("finance")
                        .getJSONArray("result")
                        .getJSONObject(0)
                        .getJSONArray("quotes");

                try (FileWriter writer = new FileWriter("sp500.csv")) {
                    writer.append("Symbol,RegularMarketPrice\n");

                    for (int i = 0; i < quotes.length(); i++) {
                        JSONObject quote = quotes.getJSONObject(i);
                        String symbol = quote.getString("symbol");
                        double regularMarketPrice = quote.optDouble("regularMarketPrice", Double.NaN);

                        writer.append(symbol).append(",")
                                .append(String.valueOf(regularMarketPrice)).append("\n");
                    }
                }

                System.out.println("The S&P 500 data has been saved in sp500.csv");
            } else {
                System.err.println("API error: " + response.message());
            }
        }
    }

    /**
     * Fetches real-time stock price details for the given stock symbol and saves it to a CSV file.
     *
     * <p>The data includes:
     * <ul>
     *   <li>Regular market price</li>
     *   <li>Previous close price</li>
     *   <li>Day high price</li>
     *   <li>Day low price</li>
     *   <li>Currency</li>
     * </ul>
     *
     * <p>The CSV file is saved in the format `stock_price_{symbol}.csv`.
     *
     * @param stockSymbol the stock symbol to fetch price details for (e.g., "AAPL")
     * @throws Exception if there is an error in the API call or file writing process
     */
    public static void getStockPrice(String stockSymbol) throws Exception {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://" + HOST + "/api/stock/get-price?region=US&symbol=" + stockSymbol)
                .get()
                .addHeader("x-rapidapi-key", API_KEY)
                .addHeader("x-rapidapi-host", HOST)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                JSONObject json = new JSONObject(responseBody);

                JSONObject price = json.getJSONObject("quoteSummary")
                        .getJSONArray("result")
                        .getJSONObject(0)
                        .getJSONObject("price");

                double regularMarketPrice = price.getJSONObject("regularMarketPrice").getDouble("raw");
                double regularMarketPreviousClose = price.getJSONObject("regularMarketPreviousClose").getDouble("raw");
                double regularMarketDayHigh = price.getJSONObject("regularMarketDayHigh").getDouble("raw");
                double regularMarketDayLow = price.getJSONObject("regularMarketDayLow").getDouble("raw");
                String currency = price.getString("currency");
                String symbol = price.getString("symbol");

                try (FileWriter writer = new FileWriter("stock_price_" + stockSymbol + ".csv")) {
                    writer.append("Symbol,RegularMarketPrice,PreviousClose,DayHigh,DayLow,Currency\n");
                    writer.append(symbol).append(",")
                            .append(String.valueOf(regularMarketPrice)).append(",")
                            .append(String.valueOf(regularMarketPreviousClose)).append(",")
                            .append(String.valueOf(regularMarketDayHigh)).append(",")
                            .append(String.valueOf(regularMarketDayLow)).append(",")
                            .append(currency).append("\n");
                }

                System.out.println("The data for the symbol " + stockSymbol + " has been saved in stock_price_" + stockSymbol + ".csv");
            } else {
                System.err.println("API error: " + response.message() + " (Code: " + response.code() + ")");
            }
        }
    }
}
