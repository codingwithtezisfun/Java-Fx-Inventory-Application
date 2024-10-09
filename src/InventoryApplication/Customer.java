package InventoryApplication;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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

public class Customer {

    @FXML
    private TextField registerCustomerName;

    @FXML
    private TextField registerCustomerEmail;

    @FXML
    private TextField registerCustomerNo;

    @FXML
    private TextArea registerCustomerAddress;

    @FXML
    private Button registerCustomerBtn;

    @FXML
    private Button deleteBtn;

    @FXML
    private ComboBox<Integer> customerIdForDelete;

    @FXML
    private TextField deleteName;

    @FXML
    private Button fetchForDeleteBtn;

    @FXML
    private TextField deletePhoneNo;

    @FXML
    private TextField phoneNumberForUpdate;

    @FXML
    private TextField emailForUpdate;

    @FXML
    private ComboBox<Integer> customerIdForUpdate;

    @FXML
    private Button fetchCustomerForUpdate;

    @FXML
    private TextArea addressUpdate;

    @FXML
    private Button updateBtn;

    @FXML
    private TextArea displayInfoTextArea;

    private static final String REGISTER_URL = "http://localhost/INVENTORY/RegisterCustomer.php";
    private static final String UPDATE_URL = "http://localhost/INVENTORY/UpdateCustomer.php";
    private static final String DELETE_URL = "http://localhost/INVENTORY/DeleteCustomer.php";
    private static final String FETCH_URL = "http://localhost/INVENTORY/FetchCustomer.php";

    @FXML
    public void initialize() {
        populateCustomerIds();
    }

    @FXML
    void registerCustomer(ActionEvent event) {
        String name = registerCustomerName.getText();
        String email = registerCustomerEmail.getText();
        String phone = registerCustomerNo.getText();
        String address = registerCustomerAddress.getText();

        if (!name.isEmpty() && !email.isEmpty() && !phone.isEmpty() && !address.isEmpty()) {
            CustomerData customer = new CustomerData(name, address, phone, email);
            Gson gson = new Gson();
            String jsonInputString = gson.toJson(customer);

            try {
                URL url = new URL(REGISTER_URL);
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
                        displayInfoTextArea.setText(response.toString());
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
    void fetchUpdateCustomer(ActionEvent event) {
        int customerId = customerIdForUpdate.getValue();

        if (customerId != 0) {
            try {
                List<CustomerData> customers = fetchCustomers();

                for (CustomerData customer : customers) {
                    if (customer.CustomerID == customerId) {
                        phoneNumberForUpdate.setText(customer.Phone);
                        emailForUpdate.setText(customer.Email);
                        addressUpdate.setText(customer.Address);
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlertError("ERROR", "Failed to fetch customer details.");
            }
        }
    }

    @FXML
    void updateCustomer(ActionEvent event) {
        int customerId = customerIdForUpdate.getValue();
        String phone = phoneNumberForUpdate.getText();
        String email = emailForUpdate.getText();
        String address = addressUpdate.getText();

        if (customerId != 0 && !phone.isEmpty() && !email.isEmpty() && !address.isEmpty()) {
            CustomerData customer = new CustomerData(customerId, "", address, phone, email);
            Gson gson = new Gson();
            String jsonInputString = gson.toJson(customer);

            try {
                URL url = new URL(UPDATE_URL);
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
                        showAlert("SUCCESS", "Customer updated successfully.");
                        populateCustomerIds();
                    } else {
                        showAlertError("ERROR", "Failed to update customer.");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlertError("ERROR", "Failed to update customer.");
            }
        } else {
            showAlertError("ERROR", "Please fill all fields.");
        }
    }

    @FXML
    void FetchDeleteCustomer(ActionEvent event) {
        int customerId = customerIdForDelete.getValue();

        if (customerId != 0) {
            try {
                List<CustomerData> customers = fetchCustomers();

                for (CustomerData customer : customers) {
                    if (customer.CustomerID == customerId) {
                        deleteName.setText(customer.CustomerName);
                        deletePhoneNo.setText(customer.Phone);
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlertError("ERROR", "Failed to fetch customer details.");
            }
        }
    }

    @FXML
    void deleteCustomer(ActionEvent event) {
        int customerId = customerIdForDelete.getValue();

        if (customerId != 0) {
            CustomerData customer = new CustomerData(customerId, "", "", "", "");
            Gson gson = new Gson();
            String jsonInputString = gson.toJson(customer);

            try {
                URL url = new URL(DELETE_URL);
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
                        showAlert("SUCCESS", "Customer deleted successfully.");
                        populateCustomerIds();
                    } else {
                        showAlertError("ERROR", "Failed to delete customer.");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlertError("ERROR", "Failed to delete customer.");
            }
        } else {
            showAlertError("ERROR", "Please select a customer ID.");
        }
    }

    private void populateCustomerIds() {
        try {
            List<CustomerData> customers = fetchCustomers();
            List<Integer> ids = new ArrayList<>();

            for (CustomerData customer : customers) {
                ids.add(customer.CustomerID);
            }

            customerIdForDelete.getItems().setAll(ids);
            customerIdForUpdate.getItems().setAll(ids);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<CustomerData> fetchCustomers() throws Exception {
        URL url = new URL(FETCH_URL);
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

    public class CustomerData {
        int CustomerID;
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

        CustomerData(int customerID, String customerName, String address, String phone, String email) {
            this.CustomerID = customerID;
            this.CustomerName = customerName;
            this.Address = address;
            this.Phone = phone;
            this.Email = email;
        }
    }
}
