package InventoryApplication;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

public class OrderSuccess {

    @FXML
    private TextField customerName;

    @FXML
    private TextField address;

    @FXML
    private TextField customerId;

    @FXML
    private TextField supplierId;

    @FXML
    private ComboBox<Integer> orderId;

    @FXML
    private Button fetch;

    @FXML
    private ComboBox<String> status;

    @FXML
    private Button submit;

    private static final String BASE_URL = "http://localhost/INVENTORY/";

    @FXML
    void initialize() {
        populateOrderIds();
        String[] statusOptions = {"Delivered", "Pending", "Cancelled"};
        status.getItems().setAll(statusOptions);
    }

    @FXML
    void submitChanges(ActionEvent event) {
        Integer selectedOrderId = orderId.getValue();
        String newStatus = status.getValue();

        if (selectedOrderId == null || newStatus == null || newStatus.isEmpty()) {
            showAlert(AlertType.WARNING, "Missing Information", null, "Please select an order ID and provide a new status.");
            return;
        }

        try {
            URL url = new URL(BASE_URL + "UpdateOrderStatus.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Construct JSON data for POST request
            JsonObject jsonInput = new JsonObject();
            jsonInput.addProperty("orderId", selectedOrderId);
            jsonInput.addProperty("newStatus", newStatus);

            OutputStream os = conn.getOutputStream();
            byte[] input = jsonInput.toString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
            os.flush();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            showAlert(AlertType.INFORMATION, "Status Update", null, "Order status updated successfully.");
            clearFields();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Update Error", null, "Failed to update order status. Please try again.");
        }
    }

    @FXML
    void fetchOrderDetails(ActionEvent event) {
        Integer selectedOrderId = orderId.getValue();

        if (selectedOrderId == null) {
            showAlert(AlertType.WARNING, "No Order Selected", null, "Please select an order ID to fetch details.");
            return;
        }

        try {
            // Construct URL to fetch order details for the selected order ID
            URL url = new URL(BASE_URL + "FetchOrderDetails.php?orderId=" + selectedOrderId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }

            Gson gson = new Gson();
            OrderData orderData = gson.fromJson(response.toString(), OrderData.class);

            // Populate fields with fetched order details
            if (orderData != null) {
                customerName.setText(orderData.CustomerName);
                address.setText(orderData.Address);
                customerId.setText(String.valueOf(orderData.CustomerID));
                supplierId.setText(String.valueOf(orderData.UserID));
                status.setValue(orderData.OrderStatus);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Fetch Error", null, "Failed to fetch order details. Please try again.");
        }
    }

    // Method to initialize order IDs in ComboBox
    private void populateOrderIds() {
        try {
            URL url = new URL(BASE_URL + "FetchOrderIds.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }

            Gson gson = new Gson();
            List<Integer> orderIds = gson.fromJson(response.toString(), new TypeToken<List<Integer>>() {}.getType());

            orderId.getItems().addAll(orderIds);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Fetch Error", null, "Failed to fetch order IDs. Please try again.");
        }
    }

    // Method to show alerts with configurable parameters
    private void showAlert(AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    public void clearFields() {
        // Clear TextFields
        customerName.clear();
        address.clear();
        supplierId.clear();
        customerId.clear();
        supplierId.clear();
        status.getSelectionModel().clearSelection();
        orderId.getSelectionModel().clearSelection();
    }
    // OrderData class to map order details from JSON
    public class OrderData {
        Integer OrderID;
        Integer CustomerID;
        String CustomerName;
        Integer UserID;
        String ProductName;
        String OrderDate;
        String ShippingDate;
        Integer Quantity;
        Double UnitPrice;
        Double TotalAmount;
        String OrderStatus;
        String Address;
    }
}
