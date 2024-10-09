package InventoryApplication;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Orders {

    private static final String BASE_URL = "http://localhost/INVENTORY/";

    @FXML
    private ComboBox<Integer> customerId;

    @FXML
    private Button fetchButton;

    @FXML
    private TextField unitPrice;

    @FXML
    private TextField quantity;

    @FXML
    private TextField customerName;

    @FXML
    private TextField address;

    @FXML
    private Button orderButton;

    @FXML
    private DatePicker orderDate;

    @FXML
    private DatePicker deliveryDate;

    @FXML
    private ComboBox<Integer> userId;

    @FXML
    private ComboBox<String> orderStatus;

    @FXML
    private ComboBox<String> productName;

    @FXML
    public void initialize() {
        populateCustomerIds();
        fetchProductNames();
        populateUserIds();
        String[] statusOptions = {"Delivered", "Pending", "Cancelled"};
        orderStatus.getItems().setAll(statusOptions);
    }

    private void populateCustomerIds() {
        try {
            List<CustomerData> customers = fetchCustomers();
            List<Integer> ids = new ArrayList<>();

            for (CustomerData customer : customers) {
                ids.add(customer.CustomerID);
            }

            customerId.getItems().setAll(ids);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void fetchCustomer(ActionEvent event) {
        Integer customerID = customerId.getValue();
        if (customerID != null) {
            try {
                List<CustomerData> customers = fetchCustomers();

                for (CustomerData customer : customers) {
                    if (customer.CustomerID.equals(customerID)) {
                        customerName.setText(customer.CustomerName);
                        address.setText(customer.Address);
                        return;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private List<CustomerData> fetchCustomers() throws Exception {
        URL url = new URL(BASE_URL + "FetchCustomer.php");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }

            Gson gson = new Gson();
            return gson.fromJson(response.toString(), new TypeToken<List<CustomerData>>() {}.getType());
        }
    }

    @FXML
    public void fetchProductNames() {
        try {
            URL url = new URL(BASE_URL + "FetchProductName.php");
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
            List<String> products = gson.fromJson(response.toString(), new TypeToken<List<String>>() {}.getType());

            productName.getItems().clear(); // Clear existing items
            productName.getItems().addAll(products); // Add fetched product names

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateUserIds() {
        try {
            URL url = new URL(BASE_URL + "FetchUserIds.php");
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
            List<Integer> userIds = gson.fromJson(response.toString(), new TypeToken<List<Integer>>() {}.getType());

            userId.getItems().clear(); // Clear existing items
            userId.getItems().addAll(userIds); // Add fetched user IDs

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void placeOrder(ActionEvent event) {
        Integer customerID = customerId.getValue();
        Integer userID = userId.getValue();
        String product = productName.getValue();
        LocalDate orderDateValue = orderDate.getValue();
        LocalDate deliveryDateValue = deliveryDate.getValue();
        Integer quantityValue = Integer.parseInt(quantity.getText().trim());
        double unitPriceValue = Double.parseDouble(unitPrice.getText());
        double totalAmount = quantityValue * unitPriceValue;
        String orderStatusValue = orderStatus.getValue();
        String addressValue = address.getText();

        if (customerID == null || userID == null || product == null || orderDateValue == null || deliveryDateValue == null ||
                orderStatusValue == null || addressValue.isEmpty() || quantityValue <= 0 || unitPriceValue <= 0) {
            showAlertError("ERROR", "Fill all required fields.");
        } else {
            try {
                // Convert LocalDate to java.sql.Date
                java.sql.Date sqlOrderDate = java.sql.Date.valueOf(orderDateValue);
                java.sql.Date sqlDeliveryDate = java.sql.Date.valueOf(deliveryDateValue);
                System.out.print("fx date" + orderDateValue);

                System.out.print("sql date" + sqlOrderDate);

                OrderData orderData = new OrderData(
                        customerID, customerName.getText(), userID, product,
                        sqlOrderDate, sqlDeliveryDate,
                        quantityValue, unitPriceValue, totalAmount, orderStatusValue, addressValue
                );

                Gson gson = new Gson();
                String jsonInputString = gson.toJson(orderData);

                URL url = new URL(BASE_URL + "PlaceOrder.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; utf-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {

                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    if (response.toString().contains("\"status\":\"success\"")) {
                        showAlert("SUCCESS", "Order placed successfully.");
                        clearFields();
                    } else {
                        showAlertError("ERROR", "Failed to place order.");
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlertError(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void clearFields() {
        // Clear TextFields
        customerName.clear();
        address.clear();
        unitPrice.clear();
        quantity.clear();
        productName.getEditor().clear();
        orderDate.setValue(null);
        deliveryDate.setValue(null);
        customerId.getSelectionModel().clearSelection();
        orderStatus.getSelectionModel().clearSelection();
    }

    // OrderData class to map order data to JSON
    public class OrderData {
        Integer CustomerID;
        String CustomerName;
        Integer UserID;
        String ProductName;
        java.sql.Date OrderDate;
        java.sql.Date ShippingDate;
        Integer Quantity;
        Double UnitPrice;
        Double TotalAmount;
        String OrderStatus;
        String Address;

        public OrderData(Integer customerID, String customerName, Integer userID, String productName, java.sql.Date orderDate,
                         java.sql.Date shippingDate, Integer quantity, Double unitPrice, Double totalAmount,
                         String orderStatus, String address) {
            this.CustomerID = customerID;
            this.CustomerName = customerName;
            this.UserID = userID;
            this.ProductName = productName;
            this.OrderDate = orderDate;
            this.ShippingDate = shippingDate;
            this.Quantity = quantity;
            this.UnitPrice = unitPrice;
            this.TotalAmount = totalAmount;
            this.OrderStatus = orderStatus;
            this.Address = address;
        }
    }

    // CustomerData class to map customer data from JSON
    public class CustomerData {
        Integer CustomerID;
        String CustomerName;
        String Address;
        String Phone;
        String Email;

        CustomerData(String customerName, String address, String phone, String email) {
            this.CustomerName = customerName;
            this.Address = address;
            this.Phone = phone;
            this.Email = email;
        }

        CustomerData(Integer customerID, String customerName, String address, String phone, String email) {
            this.CustomerID = customerID;
            this.CustomerName = customerName;
            this.Address = address;
            this.Phone = phone;
            this.Email = email;
        }
    }
}
