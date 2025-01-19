package org.example.exportData;

import com.ib.client.*;

/**
 * The {@code TWSConnector} class establishes a connection to the Interactive Brokers
 * Trader Workstation (TWS) or IB Gateway API, processes execution details, and exports
 * the data to a CSV file.
 *
 * <p>This class is responsible for:
 * <ul>
 *   <li>Establishing and managing the connection to TWS/IB Gateway</li>
 *   <li>Requesting execution details (transactions)</li>
 *   <li>Processing API messages using a custom {@link MyEWrapper}</li>
 *   <li>Exporting execution details to a CSV file</li>
 * </ul>
 *
 * <p>The connection parameters such as host, port, and client ID are configurable within the code.
 */
public class TWSConnector {
    /**
     * The custom {@link MyEWrapper} instance for handling API messages and storing execution details.
     */
    private final MyEWrapper myEWrapper;

    /**
     * The {@link EReaderSignal} instance for signaling new API messages.
     */
    private final EReaderSignal signal;

    /**
     * The {@link EClientSocket} instance for communicating with the TWS/IB Gateway.
     */
    private final EClientSocket client;

    /**
     * Initializes a new instance of {@code TWSConnector}, setting up the API components:
     * <ul>
     *   <li>A custom wrapper for processing API responses</li>
     *   <li>A signal for managing API message events</li>
     *   <li>A client socket for API communication</li>
     * </ul>
     */
    public TWSConnector() {

        myEWrapper = new MyEWrapper();
        signal = new EJavaSignal();
        client = new EClientSocket(myEWrapper, signal);
    }

    /**
     * Connects to TWS/IB Gateway, processes transaction data, and exports it to a CSV file.
     *
     * <p>This method:
     * <ul>
     *   <li>Establishes a connection to TWS/IB Gateway using predefined host, port, and client ID</li>
     *   <li>Starts the {@link EReader} to process API messages</li>
     *   <li>Sends a request to fetch execution details using {@link #requestExecutions()}</li>
     *   <li>Waits for the response and processes the data</li>
     *   <li>Exports the execution details to a CSV file using {@link CSVExporter}</li>
     *   <li>Disconnects from the TWS/IB Gateway after processing</li>
     * </ul>
     */
    public void connectAndProcess() {

        String host = "127.0.0.1"; // Localhost
        int port = 7497;           // Default TWS live port (use 7496 for Paper Trading)
        int clientId = 0;          // Unique client ID

        // Connect to TWS/IB Gateway
        client.eConnect(host, port, clientId);

        if (client.isConnected()) {
            System.out.println("Successfully connected to the TWS API!");

            startReader();

            requestExecutions();

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            CSVExporter.saveToCSV("executions.csv", myEWrapper.getTransactionDetails());

            client.eDisconnect();
            System.out.println("Disconnected from the TWS API.");
        } else {
            System.out.println("Connection failed. Check TWS/IB Gateway settings.");
        }
    }

    /**
     * Starts the {@link EReader} to process API messages in a separate thread.
     *
     * <p>The {@link EReader} continuously waits for signals, processes messages, and invokes
     * the relevant methods in {@link MyEWrapper} to handle API events.
     */
    private void startReader() {
        EReader reader = new EReader(client, signal);
        reader.start();
        new Thread(() -> {
            while (client.isConnected()) {
                signal.waitForSignal();
                try {
                    reader.processMsgs();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Sends a request to fetch execution details (transactions) from the TWS API.
     *
     * <p>An {@link ExecutionFilter} is created to specify filtering criteria for the request.
     * In this implementation, no specific filters are applied, so all recent executions
     * are retrieved.
     *
     * <p>The results are processed by the {@link MyEWrapper#execDetails(int, Contract, Execution)}
     * method and stored in the wrapper.
     */
    private void requestExecutions() {
        ExecutionFilter filter = new ExecutionFilter();

        filter.symbol("");    // Include all symbols
        filter.time("");      // Include all recent executions
        filter.secType("");   // Include all security types

        client.reqExecutions(1, filter);
    }
}
