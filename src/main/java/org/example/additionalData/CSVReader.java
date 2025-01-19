package org.example.additionalData;


import org.example.additionalData.YahooFinanceAPI;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

/**
 * The {@code CSVReader} class is responsible for processing a CSV file containing stock symbols
 * and interacting with the {@link YahooFinanceAPI} to fetch financial data.
 *
 * <p>This class provides methods to:
 * <ul>
 *   <li>Read unique stock symbols from a CSV file</li>
 *   <li>Fetch stock prices, charts, and S&P500 data for the listed symbols</li>
 * </ul>
 *
 * <p>The CSV file is expected to have the stock symbol in the third column (index 2),
 * and the first row is treated as a header and skipped during processing.
 */
public class CSVReader {

    /**
     * Processes the CSV file and fetches financial data for the stock symbols found.
     *
     * <p>This method:
     * <ul>
     *   <li>Reads unique stock symbols from the provided CSV file</li>
     *   <li>Calls {@link YahooFinanceAPI#getStockPrice(String)} to retrieve stock prices</li>
     *   <li>Calls {@link YahooFinanceAPI#getSP500()} to fetch S&P500 data</li>
     *   <li>Calls {@link YahooFinanceAPI#getStockChart(String)} to retrieve stock charts</li>
     * </ul>
     *
     * @param filePath the path to the CSV file containing stock symbols
     * @throws Exception if there is an error reading or processing the file
     */
    public void processCSVAndFetchPrices(String filePath) {
        try {
            // Read unique stock symbols from the CSV file
            Set<String> stockSymbols = readStockSymbolsFromCSV(filePath);

            // Call YahooFinanceAPI methods for each symbol
            YahooFinanceAPI yahooFinanceAPI = new YahooFinanceAPI();
            for (String stockSymbol : stockSymbols) {
                System.out.println("Processing symbol: " + stockSymbol);

                yahooFinanceAPI.getStockPrice(stockSymbol);

                YahooFinanceAPI.getSP500();

                YahooFinanceAPI.getStockChart(stockSymbol);
            }
        } catch (Exception e) {
            System.err.println("Error processing the CSV file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Reads unique stock symbols from the specified CSV file.
     *
     * <p>This method:
     * <ul>
     *   <li>Reads the CSV file line by line</li>
     *   <li>Skips the header row</li>
     *   <li>Extracts stock symbols from the third column (index 2)</li>
     *   <li>Adds the extracted symbols to a {@link Set} to ensure uniqueness</li>
     * </ul>
     *
     * @param filePath the path to the CSV file
     * @return a {@link Set} containing unique stock symbols
     * @throws Exception if there is an error reading the file
     */
    private Set<String> readStockSymbolsFromCSV(String filePath) throws Exception {
        Set<String> stockSymbols = new HashSet<>();

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    // Skip the first line (header)
                    isFirstLine = false;
                    continue;
                }


                String[] values = line.split(",");
                if (values.length > 2) {
                    // Extract the stock symbol (column 3)
                    stockSymbols.add(values[2].trim());
                }
            }
        }

        return stockSymbols;
    }
}
