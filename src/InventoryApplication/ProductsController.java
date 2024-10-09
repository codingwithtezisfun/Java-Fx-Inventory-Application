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
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ProductsController {

    @FXML
    private TextField name;

    @FXML
    private TextField unitPrize;

    @FXML
    private TextField quantity;

    @FXML
    private TextField batchNo;

    @FXML
    private ComboBox<String> category;

    @FXML
    private ComboBox<String> supplierBox;

    @FXML
    private Button submit;

    private static final String SUPPLIERS_URL = "http://localhost/INVENTORY/FetchSupplier.php";
    private static final String CATEGORY_URL = "http://localhost/INVENTORY/FetchCategory.php";
    private static final String SAVE_PRODUCT_URL = "http://localhost/INVENTORY/SaveProduct.php";

    @FXML
    public void initialize() {
        List<Supplier> suppliers = fetchSuppliers();
        List<Category> categories = fetchCategory();
        if (suppliers != null) {
            for (Supplier supplier : suppliers) {
                supplierBox.getItems().add(supplier.SupplierName);
            }
        }
        if (categories != null) {
            for (Category category : categories) {
                this.category.getItems().add(category.CategoryName);
            }
        }
    }
    public String jsonInputString;
    @FXML
    void submitPurchaseDetails(ActionEvent event) {

        String productName = name.getText();
        String productUnitPrice = unitPrize.getText();
        String productQuantity = quantity.getText();
        String productBatchNo = batchNo.getText();
        String selectedCategory = category.getValue();
        String selectedSupplier = supplierBox.getValue();



        if (!productName.isEmpty() && !productUnitPrice.isEmpty() && !productQuantity.isEmpty()
                && !productBatchNo.isEmpty() && selectedCategory != null && selectedSupplier != null) {
            Product product = new Product(productName, productUnitPrice, productQuantity,
                    productBatchNo, selectedCategory, selectedSupplier);
            Gson gson = new Gson();
            jsonInputString = gson.toJson(product);

        try {
            URL url = new URL(SAVE_PRODUCT_URL);
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
                showAlert("SUCCESS", "Data submitted succesfully.");
                name.setText("");
                unitPrize.setText("");
                quantity.setText("");
                batchNo.setText("");
                category.setValue(null);
                supplierBox.setValue(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        }else{
        	showAlertError("ERROR", "Ensure all the fields are filled correctly.");
        }
    }

    private List<Supplier> fetchSuppliers() {
        List<Supplier> suppliers = new ArrayList<>();
        try {
            URL url = new URL(SUPPLIERS_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            } else {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                Gson gson = new Gson();
                suppliers = gson.fromJson(response.toString(), new TypeToken<List<Supplier>>() {}.getType());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return suppliers;
    }

    private List<Category> fetchCategory() {
        List<Category> categories = new ArrayList<>();
        try {
            URL url = new URL(CATEGORY_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            } else {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                Gson gson = new Gson();
                categories = gson.fromJson(response.toString(), new TypeToken<List<Category>>() {}.getType());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return categories;
    }

    class Supplier {
        int SupplierID;
        String SupplierName;
    }

    class Category {
        int CategoryID;
        public String CategoryName;
    }

    class Product {
        String name;
        String unitPrize;
        String quantity;
        String batchNo;
        String category;
        String supplier;

        public Product(String name, String unitPrize, String quantity, String batchNo, String category, String supplier) {
            this.name = name;
            this.unitPrize = unitPrize;
            this.quantity = quantity;
            this.batchNo = batchNo;
            this.category = category;
            this.supplier = supplier;
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
