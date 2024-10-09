package InventoryApplication;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {

    @FXML
    private Button addProductBtn;

    @FXML
    private Button deleteProductBtn;

    @FXML
    private Button updateProductBtn;

    @FXML
    private Button placeOrderBtn;

    @FXML
    private Button confirmDeliveryBtn;

    @FXML
    private Button reportsBtn;

    @FXML
    private Button salesBtn;

    @FXML
    private Button accountInfBtn;

    @FXML
    private Button customerBtn;

    @FXML
    private Button inventoryBtn;

    @FXML
    private Button exitBtn;

    @FXML
    private Button supplierBtn;

    @FXML
    private Pane salesReportPane;

    @FXML
    private Pane purchasesPane;


    private Stage primaryStage;
    private Parent root;
    private Stage newStage;

    @FXML
    void initialize() {
        displaySalesGraph();
        displayPurchaseGraph();
    }

    @FXML
    void CloseApplication(ActionEvent event) {
        primaryStage.close();
    }

    @FXML
    void addProduct(ActionEvent event) {
        openNewStage("Products.fxml", "Add Product");
    }

    @FXML
    void confirmDelivery(ActionEvent event) {
        openNewStage("OrderSuccess.fxml", "Confirm Delivery");
    }

    @FXML
    void customerInf(ActionEvent event) {
        openNewStage("Customer.fxml", "Customer Information");
    }

    @FXML
    void deleteProduct(ActionEvent event) {
        openNewStage("DeleteProduct.fxml", "Delete Product");
    }

    @FXML
    void getAccountInfo(ActionEvent event) {
        openNewStage("OrdersReport.fxml", "Account Information"); // Replace with actual FXML file for account info
    }

    @FXML
    void inventoryInf(ActionEvent event) {
        openNewStage("Inventory.fxml", "Inventory Information");
    }

    @FXML
    void placeOrder(ActionEvent event) {
        openNewStage("Orders.fxml", "Place Order");
    }

    @FXML
    void reports(ActionEvent event) {
        openNewStage("SalesReport.fxml", "Sales Report");
    }

    @FXML
    void saleProduct(ActionEvent event) {
        openNewStage("Sales.fxml", "Sale Product");
    }

    @FXML
    void suplierInf(ActionEvent event) {
        openNewStage("Supplier.fxml", "Supplier Information");
    }

    @FXML
    void updateProduct(ActionEvent event) {
        openNewStage("UpdateProduct.fxml", "Update Product");
    }

    private void openNewStage(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            root = loader.load();
            newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.setTitle(title);
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void displaySalesGraph() {
        SalesBarChart salesBarChart = new SalesBarChart();
        salesBarChart.populateSalesGraph(salesReportPane);

    }
    @FXML
    void displayPurchaseGraph() {
        PurchaseBarChart purchaseBarChart = new PurchaseBarChart();
        purchaseBarChart.populateSalesGraph(purchasesPane);

    }

}
