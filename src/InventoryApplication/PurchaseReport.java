package InventoryApplication;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.annotations.SerializedName;

public class PurchaseReport {

    @FXML
    private TableView<Purchase> purchaseTable;

    @FXML
    private TableColumn<Purchase, Integer> purchaseId;

    @FXML
    private TableColumn<Purchase, String> productName;

    @FXML
    private TableColumn<Purchase, Integer> categoryId;

    @FXML
    private TableColumn<Purchase, Integer> supplierId;

    @FXML
    private TableColumn<Purchase, Integer> quantityBought;

    @FXML
    private TableColumn<Purchase, Double> unitPrice;

    @FXML
    private TableColumn<Purchase, String> batchNumber;

    private static final String BASE_URL = "http://localhost/INVENTORY/FetchPurchaseData.php";

    @FXML
    public void initialize() {
        initializeColumns();
        populatePurchaseTable();
    }

    private void initializeColumns() {
        purchaseId.setCellValueFactory(new PropertyValueFactory<>("purchaseID"));
        productName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        categoryId.setCellValueFactory(new PropertyValueFactory<>("categoryID"));
        supplierId.setCellValueFactory(new PropertyValueFactory<>("supplierID"));
        quantityBought.setCellValueFactory(new PropertyValueFactory<>("quantityBought"));
        unitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        batchNumber.setCellValueFactory(new PropertyValueFactory<>("batchNumber"));
    }

    private void populatePurchaseTable() {
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

                //System.out.println("response line: " + response.toString()); // Print JSON response

                Gson gson = new Gson();
                // TypeToken for Gson to correctly parse List<Purchase> from JSON array
                List<Purchase> purchaseList = gson.fromJson(response.toString(), new TypeToken<List<Purchase>>() {}.getType());

                // Print to verify list contents using overridden toString() method
                for (@SuppressWarnings("unused") Purchase purchase : purchaseList) {
                //    System.out.println("Parsed Purchase: " + purchase);  // This will now print meaningful information about each Purchase object
                }

                // Populate data into TableView
                ObservableList<Purchase> data = FXCollections.observableArrayList(purchaseList);
                purchaseTable.setItems(data);
            } else {
                System.out.println("Failed to fetch data: HTTP error code " + conn.getResponseCode());
            }

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class Purchase {
        @SerializedName("PurchaseID")
        private int purchaseID;
        @SerializedName("ProductName")
        private String productName;
        @SerializedName("CategoryID")
        private int categoryID;
        @SerializedName("SupplierID")
        private int supplierID;
        @SerializedName("QuantityBought")
        private int quantityBought;
        @SerializedName("UnitPrice")
        private double unitPrice;
        @SerializedName("BatchNumber")
        private String batchNumber;

        public int getPurchaseID() {
            return purchaseID;
        }

        public void setPurchaseID(int purchaseID) {
            this.purchaseID = purchaseID;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public int getCategoryID() {
            return categoryID;
        }

        public void setCategoryID(int categoryID) {
            this.categoryID = categoryID;
        }

        public int getSupplierID() {
            return supplierID;
        }

        public void setSupplierID(int supplierID) {
            this.supplierID = supplierID;
        }

        public int getQuantityBought() {
            return quantityBought;
        }

        public void setQuantityBought(int quantityBought) {
            this.quantityBought = quantityBought;
        }

        public double getUnitPrice() {
            return unitPrice;
        }

        public void setUnitPrice(double unitPrice) {
            this.unitPrice = unitPrice;
        }

        public String getBatchNumber() {
            return batchNumber;
        }

        public void setBatchNumber(String batchNumber) {
            this.batchNumber = batchNumber;
        }

        @Override
        public String toString() {
            return "Purchase{" +
                    "purchaseID=" + purchaseID +
                    ", productName='" + productName + '\'' +
                    ", categoryID=" + categoryID +
                    ", supplierID=" + supplierID +
                    ", quantityBought=" + quantityBought +
                    ", unitPrice=" + unitPrice +
                    ", batchNumber='" + batchNumber + '\'' +
                    '}';
        }
    }
}
