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

public class SalesReport {

    @FXML
    private TableView<Sale> salesTable;

    @FXML
    private TableColumn<Sale, Integer> saleId;

    @FXML
    private TableColumn<Sale, String> saleDate;

    @FXML
    private TableColumn<Sale, String> productName;

    @FXML
    private TableColumn<Sale, Integer> quantityBought;

    @FXML
    private TableColumn<Sale, Double> unitPrice;

    @FXML
    private TableColumn<Sale, Double> totalAmount;

    @FXML
    private TableColumn<Sale, Integer> customerId;

    private static final String BASE_URL = "http://localhost/INVENTORY/FetchSaleData.php";

    @FXML
    public void initialize() {
        initializeColumns();
        populateSalesTable();
    }

    private void initializeColumns() {
        saleId.setCellValueFactory(new PropertyValueFactory<>("saleID"));
        saleDate.setCellValueFactory(new PropertyValueFactory<>("saleDate"));
        productName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        quantityBought.setCellValueFactory(new PropertyValueFactory<>("quantityBought"));
        unitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        totalAmount.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        customerId.setCellValueFactory(new PropertyValueFactory<>("customerID"));
    }

    private void populateSalesTable() {
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

                System.out.println("response line: " + response.toString()); // Print JSON response

                Gson gson = new Gson();
                // TypeToken for Gson to correctly parse List<Sale> from JSON array
                List<Sale> salesList = gson.fromJson(response.toString(), new TypeToken<List<Sale>>() {}.getType());

                // Print to verify list contents using overridden toString() method
                for (Sale sale : salesList) {
                    System.out.println("Parsed Sale: " + sale);  // This will now print meaningful information about each Sale object
                }

                // Populate data into TableView
                ObservableList<Sale> data = FXCollections.observableArrayList(salesList);
                salesTable.setItems(data);
            } else {
                System.out.println("Failed to fetch data: HTTP error code " + conn.getResponseCode());
            }

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public class Sale {
    	@SerializedName("SaleID")
        private int saleID;
        @SerializedName("SaleDate")
        private String saleDate;
        @SerializedName("ProductName")
        private String productName;
        @SerializedName("QuantityBought")
        private int quantityBought;
        @SerializedName("UnitPrice")
        private double unitPrice;
        @SerializedName("TotalAmount")
        private double totalAmount;
        @SerializedName("CustomerID")
        private int customerID;
        public int getSaleID() {
			return saleID;
		}

		public void setSaleID(int saleID) {
			this.saleID = saleID;
		}

		public String getSaleDate() {
			return saleDate;
		}

		public void setSaleDate(String saleDate) {
			this.saleDate = saleDate;
		}

		public String getProductName() {
			return productName;
		}

		public void setProductName(String productName) {
			this.productName = productName;
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

		public double getTotalAmount() {
			return totalAmount;
		}

		public void setTotalAmount(double totalAmount) {
			this.totalAmount = totalAmount;
		}

		public int getCustomerID() {
			return customerID;
		}

		public void setCustomerID(int customerID) {
			this.customerID = customerID;
		}

        @Override
        public String toString() {
            return "Sale{" +
                    "saleID=" + saleID +
                    ", saleDate='" + saleDate + '\'' +
                    ", productName='" + productName + '\'' +
                    ", quantityBought=" + quantityBought +
                    ", unitPrice=" + unitPrice +
                    ", totalAmount=" + totalAmount +
                    ", customerID=" + customerID +
                    '}';
        }


    }

}
