package InventoryApplication;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
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
import com.google.gson.reflect.TypeToken;

public class Supplier {

    @FXML
    private TextField registerSupplierName;

    @FXML
    private TextField registerSupplierEmail;

    @FXML
    private TextField registerSupplierPhoneNo;

    @FXML
    private TextArea registerSupplierAddress;

    @FXML
    private Button registerSupplierBtn;

    @FXML
    private Button deleteSupplierBtn;

    @FXML
    private ComboBox<Integer> fetchSupplierIdForDelete;

    @FXML
    private TextField supplierNameForDelete;

    @FXML
    private Button fetchSupplierForDeleteBtn;

    @FXML
    private TextField phoneNumberForDelete;

    @FXML
    private TextField phoneUpdate;

    @FXML
    private TextField emailUpdate;

    @FXML
    private ComboBox<Integer> fetchSupplierIdForUpdate;

    @FXML
    private Button fetchUpdateBtn;

    @FXML
    private TextArea addressUpdate;

    @FXML
    private Button updateSupplierBtn;

    @FXML
    private TextArea displayTextArea;

    private static final String BASE_URL = "http://localhost/INVENTORY/";

    @FXML
    void registerSupplier(ActionEvent event) {
        String name = registerSupplierName.getText();
        String email = registerSupplierEmail.getText();
        String phone = registerSupplierPhoneNo.getText();
        String address = registerSupplierAddress.getText();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            showAlertError("ERROR", "Please fill all fields.");
            return;
        }else{

        Suppliers supplier = new Suppliers(name, address, phone, email);
        Gson gson = new Gson();
        String jsonInputString = gson.toJson(supplier);

        try {
            URL url = new URL(BASE_URL + "RegisterSupplier.php");
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
                    showAlert("SUCCESS", "Supplier registered successfully.");
                    displayTextArea.setText(response.toString());
                    fetchSupplierIdForDelete.getItems().clear();
                    fetchSupplierIdForUpdate.getItems().clear();
                    registerSupplierName.setText("");
                    registerSupplierEmail.setText("");
                    registerSupplierPhoneNo.setText("");
                    registerSupplierAddress.setText("");

                    populateSupplierIds();
                } else {
                    showAlertError("ERROR", "Failed to register supplier.");
                }
            }
        } catch (Exception e) {
            displayTextArea.setText("Error: " + e.getMessage());
            e.printStackTrace();
            showAlertError("ERROR", "Failed to register supplier.");
        }
        }
    }

    @FXML
    void updateSupplier(ActionEvent event) {
        Integer id = fetchSupplierIdForUpdate.getValue();
        String email = emailUpdate.getText();
        String phone = phoneUpdate.getText();
        String address = addressUpdate.getText();

        if (id != null && !email.isEmpty() && !phone.isEmpty() && !address.isEmpty()) {
            Suppliers supplier = new Suppliers(id, null, email, phone,address);
            Gson gson = new Gson();
            String jsonInputString = gson.toJson(supplier);

            try {
                URL url = new URL(BASE_URL + "UpdateSupplier.php");
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
                    displayTextArea.setText(response.toString());
                }
            } catch (Exception e) {
                displayTextArea.setText("Error: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            displayTextArea.setText("Ensure all fields are filled correctly.");
        }
    }

    @FXML
    void deleteSupplier(ActionEvent event) {
        Integer id = fetchSupplierIdForDelete.getValue();

        if (id != null) {
            Suppliers supplier = new Suppliers(id, null, null, null, null);
            Gson gson = new Gson();
            String jsonInputString = gson.toJson(supplier);

            try {
                URL url = new URL(BASE_URL + "DeleteSupplier.php");
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
                    displayTextArea.setText(response.toString());
                    fetchSupplierIdForDelete.getItems().clear();
                    fetchSupplierIdForUpdate.getItems().clear();
                    populateSupplierIds();
                }
            } catch (Exception e) {
                displayTextArea.setText("Error: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            displayTextArea.setText("Select a supplier ID to delete.");
        }
    }

    @FXML
    void fetchSupplierForDelete(ActionEvent event) {
        Integer id = fetchSupplierIdForDelete.getValue();

        if (id != null) {
            try {
                URL url = new URL(BASE_URL + "FetchSupplier.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                List<Suppliers> suppliers = new Gson().fromJson(response.toString(), new TypeToken<List<Suppliers>>() {}.getType());
                for (Suppliers supplier : suppliers) {
                    if (supplier.getSupplierID().equals(id)) {
                        supplierNameForDelete.setText(supplier.getSupplierName());
                        phoneNumberForDelete.setText(supplier.getPhone());
                    }
                }

            } catch (Exception e) {
                displayTextArea.setText("Error: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            displayTextArea.setText("Select a supplier ID to fetch.");
        }
    }

    @FXML
    void fetchSupplierForUpdate(ActionEvent event) {
        Integer id = fetchSupplierIdForUpdate.getValue();

        if (id != null) {
            try {
                URL url = new URL(BASE_URL + "FetchSupplier.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                List<Suppliers> suppliers = new Gson().fromJson(response.toString(), new TypeToken<List<Suppliers>>() {}.getType());
                for (Suppliers supplier : suppliers) {
                    if (supplier.getSupplierID().equals(id)) {
                        registerSupplierName.setText(supplier.getSupplierName());
                        emailUpdate.setText(supplier.getEmail());
                        phoneUpdate.setText(supplier.getPhone());
                        addressUpdate.setText(supplier.getAddress());
                    }
                }

            } catch (Exception e) {
                displayTextArea.setText("Error: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            displayTextArea.setText("Select a supplier ID to fetch.");
        }
    }

    private void populateSupplierIds() {
        try {
            URL url = new URL(BASE_URL + "FetchSupplier.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }

            List<Suppliers> suppliers = new Gson().fromJson(response.toString(), new TypeToken<List<Suppliers>>() {}.getType());
            for (Suppliers supplier :suppliers) {
                fetchSupplierIdForDelete.getItems().add(supplier.getSupplierID());
                fetchSupplierIdForUpdate.getItems().add(supplier.getSupplierID());
            }

        } catch (Exception e) {
            displayTextArea.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void initialize() {
        populateSupplierIds();
    }

    // Supplier class to map JSON data
    public static class Suppliers {
        private Integer SupplierID;
        private String SupplierName;
        private String Address;
        private String Phone;
        private String Email;

        // Constructor for registration
        public Suppliers(String supplierName, String address, String phone, String email) {
            this.SupplierName = supplierName;
            this.Address = address;
            this.Phone = phone;
            this.Email = email;
        }

        // Constructor for update
        public Suppliers(Integer supplierID, String supplierName, String address, String phone, String email) {
            this.SupplierID = supplierID;
            this.SupplierName = supplierName;
            this.Address = address;
            this.Phone = phone;
            this.Email = email;
        }

        // Getters
        public Integer getSupplierID() {
            return SupplierID;
        }

        public String getSupplierName() {
            return SupplierName;
        }

        public String getAddress() {
            return Address;
        }

        public String getPhone() {
            return Phone;
        }

        public String getEmail() {
            return Email;
        }
    }

    private void showAlert(String title, String message) {
    	 Alert alert = new Alert(AlertType.CONFIRMATION);
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
}

