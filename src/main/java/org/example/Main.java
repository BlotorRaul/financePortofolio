package org.example;


import org.example.additionalData.CSVReader;
import org.example.additionalData.YahooFinanceAPI;
import org.example.exportData.TWSConnector;
import org.example.financialCalc.FinancialDashboard;
import org.example.financialCalc.FinancialManager;
import org.example.financialCalc.ObtaintAllData;

import javax.swing.*;
import java.util.Map;
/*ExecId,Date,StockSymbol,TransactionType,Quantity,PricePerShare,TotalAmount
00012ec5.676a4251.01.01,20241224 11:46:53 EET,AAPL,BOT,100.0000000000000000,255.45,25545.00
00012ec5.676a4255.01.01,20241224 11:47:37 EET,AAPL,BOT,40.0000000000000000,255.45,10218.00
00012ec5.676a4256.01.01,20241224 11:47:54 EET,AAPL,BOT,8.0000000000000000,255.45,2043.60
*/
public class Main {
    public static void main(String[] args) {
        TWSConnector twsConnector = new TWSConnector();
        twsConnector.connectAndProcess();

        String csvFilePath = "D:\\JAVA\\financePortofolio\\executions.csv";

        // Creează obiectul CSVReader și procesează fișierul CSV
        CSVReader csvReader = new CSVReader();
        csvReader.processCSVAndFetchPrices(csvFilePath);

        try {
            System.out.println("=== Stock Chart ===");
            YahooFinanceAPI.getStockChart("TSLA");

            System.out.println("\n=== S&P 500 ===");
            YahooFinanceAPI.getSP500();

            System.out.println("\n=== Stock Price ===");
            YahooFinanceAPI.getStockPrice("TSLA");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Instanțierea FinancialManager pentru a obține datele
        ObtaintAllData dataManager = new ObtaintAllData();

        // Fișierele CSV
        String transactionsCsvFile = "executions.csv";
        String stockChartCsvFile = "stock_chart_AAPL.csv";
        String sp500CsvFile = "sp500.csv";
        String stockPriceCsvFile = "stock_price_AAPL.csv";

        // Crearea FinancialManager și obținerea datelor
        FinancialManager financialManager = new FinancialManager(dataManager, transactionsCsvFile, stockChartCsvFile, sp500CsvFile, stockPriceCsvFile);
        Map<String, Object> dashboardData = financialManager.getDashboardData();

        // Lansarea interfeței grafice
        SwingUtilities.invokeLater(() -> {
            FinancialDashboard dashboard = new FinancialDashboard(dashboardData);
            dashboard.setVisible(true);
        });
    }

}