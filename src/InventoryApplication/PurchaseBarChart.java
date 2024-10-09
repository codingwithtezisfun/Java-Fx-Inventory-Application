package InventoryApplication;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Pane;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class PurchaseBarChart {

    private static final String BASE_URL = "http://localhost/INVENTORY/FetchPurchasesReport.php"; // Adjust URL as per your server setup

    private BarChart<String, Number> salesGraph2;

    public PurchaseBarChart() {
        // Define axes
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Product Name");
        yAxis.setLabel("Total Spending");

        // Create bar chart
        salesGraph2 = new BarChart<>(xAxis, yAxis);
        salesGraph2.setTitle("Total Spending by Product");
        salesGraph2.setMinSize(497, 460);
        salesGraph2.setMaxSize(497, 460);
        salesGraph2.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

    }

    public void populateSalesGraph(Pane pane) {
        try {
            URL url = new URL(BASE_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() == 200) { // HTTP success
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                // Parse JSON data using Gson
                Gson gson = new Gson();
                TypeToken<List<TotalSpendingData>> token = new TypeToken<List<TotalSpendingData>>() {};
                List<TotalSpendingData> totalSpendingDataList = gson.fromJson(response.toString(), token.getType());

                // Populate chart data
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName("Total Spending");

                for (TotalSpendingData data : totalSpendingDataList) {
                    series.getData().add(new XYChart.Data<>(data.getProductName(), data.getTotalSpending()));
                }

                // Update chart with data
                salesGraph2.getData().add(series);

                // Clear existing children and add chart to the pane
                pane.getChildren().clear();
                pane.getChildren().add(salesGraph2);

            } else {
                System.out.println("Failed to fetch data: HTTP error code " + conn.getResponseCode());
            }

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // POJO class for holding total spending data
    public static class TotalSpendingData {
        private String ProductName;
        private double TotalSpending;

        public String getProductName() {
            return ProductName;
        }

        public void setProductName(String productName) {
            ProductName = productName;
        }

        public double getTotalSpending() {
            return TotalSpending;
        }

        public void setTotalSpending(double totalSpending) {
            TotalSpending = totalSpending;
        }
    }
}
