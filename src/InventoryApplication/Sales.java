package InventoryApplication;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.chart.BarChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Sales {

    @FXML
    private Button registerBtn;

    @FXML
    private TextField registerNewCustomerName;

    @FXML
    private TextField registerNewCustomerPhone;

    @FXML
    private TextField registerNewCustomerEmail;

    @FXML
    private TextField registerNewCustomerAddress;

    @FXML
    private TextField customerName;

    @FXML
    private TextField totalAmount;

    @FXML
    private TextField unitPrice;

    @FXML
    private TextField quantity;

    @FXML
    private TextField fetchedCustomerEmail;

    @FXML
    private TextField fetchedCustomerPhoneNo;

    @FXML
    private TextArea textArea;

    @FXML
    private Button submit;

    @FXML
    private Button fetchButton;

    @FXML
    private ComboBox<Integer> fetchCustomerUsingID;

    @FXML
    private ComboBox<String> fetchProductByName;

    @FXML
    private BarChart<?, ?> salesGraph;

    private static final String BASE_URL = "http://localhost/INVENTORY/";

    @FXML
    public void initialize() {
        populateCustomerIds();
        fetchProductNames();
    }

    @FXML
    void fetchCustomerDetails(ActionEvent event) {
        Integer customerID = fetchCustomerUsingID.getValue();
        if (customerID != null) {
            try {
                List<CustomerData> customers = fetchCustomers();

                for (CustomerData customer : customers) {
                    if (customer.CustomerID.equals(customerID)) {
                        customerName.setText(customer.CustomerName);
                        fetchedCustomerEmail.setText(customer.Email);
                        fetchedCustomerPhoneNo.setText(customer.Phone);
                        return;
                    }
                }
                textArea.setText("Customer not found");
            } catch (Exception e) {
                e.printStackTrace();
                textArea.setText("Error: " + e.getMessage());
            }
        } else {
            textArea.setText("Please select a customer ID.");
        }
    }

    @FXML
    void registerNewCustomer(ActionEvent event) {
        String name = registerNewCustomerName.getText();
        String email = registerNewCustomerEmail.getText();
        String phone = registerNewCustomerPhone.getText();
        String address = registerNewCustomerAddress.getText();

        if (!name.isEmpty() && !email.isEmpty() && !phone.isEmpty() && !address.isEmpty()) {
            CustomerData customer = new CustomerData(name, address, phone, email);
            Gson gson = new Gson();
            String jsonInputString = gson.toJson(customer);

            try {
                URL url = new URL(BASE_URL + "RegisterCustomer.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; utf-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    if (response.toString().contains("\"status\":\"success\"")) {
                        showAlert("SUCCESS", "Customer registered successfully.");
                        textArea.setText(response.toString());
                        populateCustomerIds();
                    } else {
                        showAlertError("ERROR", "Failed to register customer.");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlertError("ERROR", "Failed to register customer.");
            }
        } else {
            showAlertError("ERROR", "Please fill all fields.");
        }
    }

    @FXML
    void submitPurchaseDetails(ActionEvent event) {
    	String productName = (String) fetchProductByName.getValue();
        int quantityBought = Integer.parseInt(quantity.getText());
        double unitPriceValue = Double.parseDouble(unitPrice.getText());
        double totalAmountValue = unitPriceValue * quantityBought;
        Integer customerID = fetchCustomerUsingID.getValue();

        if (productName != null && customerID != null) {
        	SaleData sale = new SaleData(productName, quantityBought, unitPriceValue, totalAmountValue, customerID);
            Gson gson = new Gson();
            String jsonInputString = gson.toJson(sale);

            try {
                URL url = new URL(BASE_URL + "SubmitPurchaseDetails.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; utf-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    if (response.toString().contains("\"status\":\"success\"")) {
                        showAlert("SUCCESS", "Purchase submitted successfully.");
                        clearFields();
                    } else {
                        showAlertError("ERROR", "Failed to submit purchase.");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlertError("ERROR", "Failed to submit purchase.");
            }
        } else {
            showAlertError("ERROR", "Please select a product and customer.");
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

            // Assuming fetchProductByName is a ComboBox<String>
            fetchProductByName.getItems().clear(); // Clear existing items
            fetchProductByName.getItems().addAll(products); // Add fetched product names

        } catch (Exception e) {
            e.printStackTrace();
            textArea.setText("Error fetching product names: " + e.getMessage());
        }
    }
    private void populateCustomerIds() {
        try {
            List<CustomerData> customers = fetchCustomers();
            List<Integer> ids = new ArrayList<>();

            for (CustomerData customer : customers) {
                ids.add(customer.CustomerID);
            }

            fetchCustomerUsingID.getItems().setAll(ids);
        } catch (Exception e) {
            e.printStackTrace();
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
        customerName.clear();
        totalAmount.clear();
        unitPrice.clear();
        quantity.clear();
    }
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
    public class SaleData {
        private String ProductName;
        private Integer QuantityBought;
        private Double UnitPrice;
        private Double TotalAmount;
        private Integer CustomerID;

        public SaleData(String productName, Integer quantityBought, Double unitPrice, Double totalAmount, Integer customerID) {
            this.ProductName = productName;
            this.QuantityBought = quantityBought;
            this.UnitPrice = unitPrice;
            this.TotalAmount = totalAmount;
            this.CustomerID = customerID;
        }

        // Getters and Setters
        public String getProductName() {
            return ProductName;
        }

        public void setProductName(String productName) {
            this.ProductName = productName;
        }

        public Integer getQuantityBought() {
            return QuantityBought;
        }

        public void setQuantityBought(Integer quantityBought) {
            this.QuantityBought = quantityBought;
        }

        public Double getUnitPrice() {
            return UnitPrice;
        }

        public void setUnitPrice(Double unitPrice) {
            this.UnitPrice = unitPrice;
        }

        public Double getTotalAmount() {
            return TotalAmount;
        }

        public void setTotalAmount(Double totalAmount) {
            this.TotalAmount = totalAmount;
        }

        public Integer getCustomerID() {
            return CustomerID;
        }

        public void setCustomerID(Integer customerID) {
            this.CustomerID = customerID;
        }

        // toString method for debugging/logging
        @Override
        public String toString() {
            return "SaleData{" +
                    "ProductName='" + ProductName + '\'' +
                    ", QuantityBought=" + QuantityBought +
                    ", UnitPrice=" + UnitPrice +
                    ", TotalAmount=" + TotalAmount +
                    ", CustomerID=" + CustomerID +
                    '}';
        }
    }
}
