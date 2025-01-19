package org.example.financialCalc;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * The {@code FinancialDashboard} class provides a graphical user interface (GUI)
 * to display financial data including ROI, cumulative ROI, comparisons with S&P500, and the Sharpe Ratio.
 *
 * <p>This class creates various charts and visualizations to help users analyze financial performance:
 * <ul>
 *   <li>A line chart for daily ROI</li>
 *   <li>A line chart for cumulative ROI</li>
 *   <li>A comparison chart of portfolio ROI vs. S&P500 ROI</li>
 *   <li>A display for the Sharpe Ratio</li>
 * </ul>
 *
 * <p>The class leverages the JFreeChart library for generating charts and uses Swing for the GUI.
 */
public class FinancialDashboard extends JFrame {

    /**
     * Constructs a new {@code FinancialDashboard} and initializes the GUI with the provided financial data.
     *
     * @param dashboardData a {@link Map} containing financial data required for the dashboard:
     *                      <ul>
     *                          <li>"dates": A {@link List} of dates as strings</li>
     *                          <li>"roiValues": A {@link List} of daily ROI values</li>
     *                          <li>"cumulativeROI": A {@link List} of cumulative ROI values</li>
     *                          <li>"sp500Values": A {@link List} of S&P500 ROI values</li>
     *                          <li>"sharpeRatio": A {@code double} representing the Sharpe Ratio</li>
     *                      </ul>
     */
    public FinancialDashboard(Map<String, Object> dashboardData) {
        setTitle("Financial Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Extract data from the map
        List<String> dates = (List<String>) dashboardData.get("dates");
        List<Double> roiValues = (List<Double>) dashboardData.get("roiValues");
        List<Double> cumulativeROI = (List<Double>) dashboardData.get("cumulativeROI");
        List<Double> sp500Values = (List<Double>) dashboardData.get("sp500Values");
        double sharpeRatio = (double) dashboardData.get("sharpeRatio");

        // Main panel to hold the charts
        JPanel mainPanel = new JPanel(new GridLayout(2, 2));
        mainPanel.add(createROIGraph(dates, roiValues));
        mainPanel.add(createCumulativeROIGraph(dates, cumulativeROI));
        mainPanel.add(createComparisonGraph(dates, roiValues, sp500Values));
        mainPanel.add(createSharpeRatioDisplay(sharpeRatio));

        setContentPane(mainPanel);
    }

    /**
     * Creates a panel containing a line chart for daily ROI.
     *
     * @param dates     a {@link List} of dates as strings
     * @param roiValues a {@link List} of daily ROI values
     * @return a {@link JPanel} containing the chart
     */
    private JPanel createROIGraph(List<String> dates, List<Double> roiValues) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        int dataSize = Math.min(dates.size(), roiValues.size());
        for (int i = 0; i < dataSize; i++) {
            dataset.addValue(roiValues.get(i), "ROI", dates.get(i));
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "Daily ROI", "Date", "ROI (%)", dataset
        );

        return new ChartPanel(chart);
    }

    /**
     * Creates a panel containing a line chart for cumulative ROI.
     *
     * @param dates         a {@link List} of dates as strings
     * @param cumulativeROI a {@link List} of cumulative ROI values
     * @return a {@link JPanel} containing the chart
     */
    private JPanel createCumulativeROIGraph(List<String> dates, List<Double> cumulativeROI) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        int dataSize = Math.min(dates.size(), cumulativeROI.size());
        for (int i = 0; i < dataSize; i++) {
            dataset.addValue(cumulativeROI.get(i), "Cumulative ROI", dates.get(i));
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "Cumulative ROI", "Date", "Cumulative ROI (%)", dataset
        );

        return new ChartPanel(chart);
    }

    /**
     * Creates a panel containing a comparison chart for portfolio ROI vs. S&P500 ROI.
     *
     * @param dates      a {@link List} of dates as strings
     * @param roiValues  a {@link List} of portfolio ROI values
     * @param sp500Values a {@link List} of S&P500 ROI values
     * @return a {@link JPanel} containing the comparison chart
     */
    private JPanel createComparisonGraph(List<String> dates, List<Double> roiValues, List<Double> sp500Values) {
        XYSeries portfolioSeries = new XYSeries("Portfolio ROI");
        XYSeries sp500Series = new XYSeries("S&P500 ROI");

        int dataSize = Math.min(Math.min(dates.size(), roiValues.size()), sp500Values.size());
        for (int i = 0; i < dataSize; i++) {
            portfolioSeries.add(i, roiValues.get(i));
            sp500Series.add(i, sp500Values.get(i));
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(portfolioSeries);
        dataset.addSeries(sp500Series);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Comparison with S&P500", "Date Index", "ROI (%)", dataset
        );

        return new ChartPanel(chart);
    }

    /**
     * Creates a panel displaying the Sharpe Ratio.
     *
     * @param sharpeRatio the Sharpe Ratio as a {@code double}
     * @return a {@link JPanel} containing the Sharpe Ratio display
     */
    private JPanel createSharpeRatioDisplay(double sharpeRatio) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(String.format("Sharpe Ratio: %.4f", sharpeRatio), SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    /**
     * The main method to launch the Financial Dashboard.
     *
     * <p>This method:
     * <ul>
     *   <li>Initializes the {@code FinancialManager} to fetch data</li>
     *   <li>Passes the data to {@code FinancialDashboard} for visualization</li>
     * </ul>
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {

        ObtaintAllData dataManager = new ObtaintAllData();

        String transactionsCsvFile = "executions.csv";
        String stockChartCsvFile = "stock_chart_AAPL.csv";
        String sp500CsvFile = "sp500.csv";
        String stockPriceCsvFile = "stock_price_AAPL.csv";

        FinancialManager financialManager = new FinancialManager(dataManager, transactionsCsvFile, stockChartCsvFile, sp500CsvFile, stockPriceCsvFile);
        Map<String, Object> dashboardData = financialManager.getDashboardData();

        SwingUtilities.invokeLater(() -> {
            FinancialDashboard dashboard = new FinancialDashboard(dashboardData);
            dashboard.setVisible(true);
        });
    }
}
