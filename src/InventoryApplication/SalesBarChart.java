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

public class SalesBarChart {

    private static final String BASE_URL = "http://localhost/INVENTORY/FetchSalesReport.php"; // Adjust URL as per your server setup

    private BarChart<String, Number> salesGraph;

    public SalesBarChart() {
        // Define axes
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Product Name");
        yAxis.setLabel("Total Sales");

        // Create bar chart
        salesGraph = new BarChart<>(xAxis, yAxis);
        salesGraph.setTitle("SALES REPORTS");
        salesGraph.setMinSize(497, 460);
        salesGraph.setMaxSize(497, 460);


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
                TypeToken<List<SalesData>> token = new TypeToken<List<SalesData>>() {};
                List<SalesData> salesDataList = gson.fromJson(response.toString(), token.getType());

                // Populate chart data
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName("Total Sales");

                for (SalesData data : salesDataList) {
                    series.getData().add(new XYChart.Data<>(data.getProductName(), data.getTotalSales()));
                }

                // Update chart with data
                salesGraph.getData().add(series);

                // Clear existing children and add chart to the pane
                pane.getChildren().clear();
                pane.getChildren().add(salesGraph);

            } else {
                System.out.println("Failed to fetch data: HTTP error code " + conn.getResponseCode());
            }

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // POJO class for holding sales data
    public static class SalesData {
        private String ProductName;
        private double TotalSales;

        public String getProductName() {
            return ProductName;
        }

        public void setProductName(String productName) {
            ProductName = productName;
        }

        public double getTotalSales() {
            return TotalSales;
        }

        public void setTotalSales(double totalSales) {
            TotalSales = totalSales;
        }
    }
}
