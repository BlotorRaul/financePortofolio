package org.example.financialCalc;

import java.util.List;

/**
 * The {@code FinancialMetricsCalculator} class provides static methods for calculating
 * financial metrics such as ROI, volatility, and Sharpe Ratio.
 *
 * <p>This class is designed for use in financial analysis applications to compute key metrics
 * based on asset prices, transactions, and market data.
 */
public class FinancialMetricsCalculator {

    /**
     * The risk-free rate used in the Sharpe Ratio calculation (e.g., 2%).
     */
    private static final double RISK_FREE_RATE = 0.02;

    /**
     * Calculates the Return on Investment (ROI) for a given transaction.
     *
     * <p>Formula:
     * <pre>
     * ROI = ((Current Price - Purchase Price) * Quantity / Total Amount) * 100
     * </pre>
     *
     * @param regularMarketPrice the current market price of the asset
     * @param pricePerShare      the purchase price per share
     * @param totalAmount        the total amount invested in the transaction
     * @param quantity           the number of shares purchased
     * @return the ROI as a percentage
     */
    public static double calculateROI(double regularMarketPrice, double pricePerShare, double totalAmount, double quantity) {
        return ((regularMarketPrice - pricePerShare) * quantity / totalAmount) * 100;
    }

    /**
     * Calculates the volatility (standard deviation of returns) based on a list of asset prices.
     *
     * <p>Formula:
     * <pre>
     * Volatility = sqrt(sum((price - mean)^2) / (n - 1))
     * </pre>
     *
     * @param prices a {@link List} of asset prices
     * @return the calculated volatility
     * @throws IllegalArgumentException if the list is null or contains fewer than two elements
     */
    public static double calculateVolatility(List<Double> prices) {
        if (prices == null || prices.size() < 2) {
            throw new IllegalArgumentException("The price list must contain at least 2 elements.");
        }

        double mean = prices.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double variance = prices.stream()
                .mapToDouble(price -> Math.pow(price - mean, 2))
                .sum() / (prices.size() - 1);

        return Math.sqrt(variance);
    }

    /**
     * Calculates the Sharpe Ratio, which measures the risk-adjusted return of an investment.
     *
     * <p>Formula:
     * <pre>
     * Sharpe Ratio = (ROI - Risk-Free Rate) / Volatility
     * </pre>
     *
     * @param roi        the Return on Investment (ROI) as a percentage
     * @param volatility the calculated volatility
     * @return the Sharpe Ratio
     * @throws IllegalArgumentException if volatility is zero
     */
    public static double calculateSharpeRatio(double roi, double volatility) {
        if (volatility == 0) {
            throw new IllegalArgumentException("Volatility cannot be zero.");
        }

        double roiAnnualized = roi / 100; // Convert ROI to fractional form
        return (roiAnnualized - RISK_FREE_RATE) / volatility;
    }
}
