package InventoryApplication;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert.AlertType;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class DeleteProduct {

    @FXML
    private ComboBox<String> batchNo;

    @FXML
    private Button deleteBtn;

    private static final String BATCH_URL = "http://localhost/INVENTORY/FetchBarchNo.php";
    private static final String DELETE_PRODUCT_URL = "http://localhost/INVENTORY/DeleteProduct.php";

    @FXML
    public void initialize() {
        populateBatchNumbers();
    }

    @FXML
    void deleteProduct(ActionEvent event) {
        String selectedBatch = batchNo.getValue();

        if (selectedBatch != null && !selectedBatch.isEmpty()) {
            Product product = new Product(selectedBatch);
            Gson gson = new Gson();
            String jsonInputString = gson.toJson(product);

            try {
                URL url = new URL(DELETE_PRODUCT_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; utf-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    showAlert("SUCCESS", "Data submitted successfully.");
                    batchNo.setValue(null);
                    populateBatchNumbers(); // Refresh ComboBox after deletion
                } else {
                    showAlertError("ERROR", "Failed to delete product. HTTP error code: " + responseCode);
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlertError("ERROR", "Failed to delete product.");
            }
        } else {
            showAlertError("ERROR", "Please select a batch number.");
        }
    }

    private void populateBatchNumbers() {
        try {
            List<String> batchNumbers = fetchBatchNumbers();

            if (batchNumbers != null) {
                batchNo.getItems().clear();
                for (String batchNumber : batchNumbers) {
                    batchNo.getItems().add(batchNumber);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlertError("ERROR", "Failed to fetch batch numbers.");
        }
    }

    private List<String> fetchBatchNumbers() {
        List<String> batchNumbers = new ArrayList<>();
        try {
            URL url = new URL(BATCH_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                Gson gson = new Gson();
                batchNumbers = gson.fromJson(response.toString(), new TypeToken<List<String>>() {}.getType());
            } else {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return batchNumbers;
    }

    class Product {
        String batch;

        public Product(String batch) {
            this.batch = batch;
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
