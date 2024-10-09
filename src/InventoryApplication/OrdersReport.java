package InventoryApplication;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.sql.*;
import java.time.LocalDate;

public class OrdersReport {

    @FXML
    private TableView<OrderData> tableView;

    @FXML
    private TableColumn<OrderData, Integer> orderId;

    @FXML
    private TableColumn<OrderData, Integer> customerId;

    @FXML
    private TableColumn<OrderData, String> customerName;

    @FXML
    private TableColumn<OrderData, Integer> userId;

    @FXML
    private TableColumn<OrderData, String> productName;

    @FXML
    private TableColumn<OrderData, LocalDate> orderDate;

    @FXML
    private TableColumn<OrderData, LocalDate> deliveryDate;

    @FXML
    private TableColumn<OrderData, Integer> quantity;

    @FXML
    private TableColumn<OrderData, Double> unitPrice;

    @FXML
    private TableColumn<OrderData, Double> totalAmount;

    @FXML
    private TableColumn<OrderData, String> orderStatus;

    @FXML
    private TableColumn<OrderData, String> address;

    private Connection connection;

    @FXML
    void initialize() {
        // Initialize table columns
        orderId.setCellValueFactory(cellData -> cellData.getValue().orderIdProperty().asObject());
        customerId.setCellValueFactory(cellData -> cellData.getValue().customerIdProperty().asObject());
        customerName.setCellValueFactory(cellData -> cellData.getValue().customerNameProperty());
        userId.setCellValueFactory(cellData -> cellData.getValue().userIdProperty().asObject());
        productName.setCellValueFactory(cellData -> cellData.getValue().productNameProperty());
        orderDate.setCellValueFactory(cellData -> cellData.getValue().orderDateProperty());
        deliveryDate.setCellValueFactory(cellData -> cellData.getValue().deliveryDateProperty());
        quantity.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
        unitPrice.setCellValueFactory(cellData -> cellData.getValue().unitPriceProperty().asObject());
        totalAmount.setCellValueFactory(cellData -> cellData.getValue().totalAmountProperty().asObject());
        orderStatus.setCellValueFactory(cellData -> cellData.getValue().orderStatusProperty());
        address.setCellValueFactory(cellData -> cellData.getValue().addressProperty());

        // Initialize TableView with data
        tableView.setItems(fetchOrderData());
    }

    private ObservableList<OrderData> fetchOrderData() {
        ObservableList<OrderData> orderList = FXCollections.observableArrayList();
        try {
            // Establish connection to your database
            connection = DriverManager.getConnection("jdbc:mysql://localhost/inventory", "root", "");

            // SQL query to fetch data from orders table
            String query = "SELECT * FROM orders";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

            	// Process each row from the result set
            	while (rs.next()) {
            	    OrderData order = new OrderData();
            	    order.setOrderId(rs.getInt("OrderID"));
            	    order.setCustomerId(rs.getInt("CustomerID"));
            	    order.setCustomerName(rs.getString("CustomerName"));
            	    order.setUserId(rs.getInt("UserID"));
            	    order.setProductName(rs.getString("ProductName"));

            	    // Handling zero date values
            	    Date orderDate = rs.getDate("OrderDate");
            	    if (orderDate != null && !rs.wasNull()) {
            	        if (orderDate.toLocalDate().equals(LocalDate.of(1, 1, 1))) {
            	            order.setOrderDate(null); // or set to a default date
            	        } else {
            	            order.setOrderDate(orderDate.toLocalDate());
            	        }
            	    } else {
            	        order.setOrderDate(null); // or set to a default date
            	    }

            	    Date shippingDate = rs.getDate("ShippingDate");
            	    if (shippingDate != null && !rs.wasNull()) {
            	        if (shippingDate.toLocalDate().equals(LocalDate.of(1, 1, 1))) {
            	            order.setDeliveryDate(null); // or set to a default date
            	        } else {
            	            order.setDeliveryDate(shippingDate.toLocalDate());
            	        }
            	    } else {
            	        order.setDeliveryDate(null); // or set to a default date
            	    }

            	    order.setQuantity(rs.getInt("Quantity"));
            	    order.setUnitPrice(rs.getDouble("UnitPrice"));
            	    order.setTotalAmount(rs.getDouble("TotalAmount"));
            	    order.setOrderStatus(rs.getString("OrderStatus"));
            	    order.setAddress(rs.getString("Address"));
            	    orderList.add(order);
            	}
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", null, "Failed to fetch order data from database.");
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return orderList;
    }

    // Method to show alert dialogs
    private void showAlert(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

}
