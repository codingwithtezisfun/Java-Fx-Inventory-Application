<?php
require 'Connection.php'; // Ensure this line is correct and Connection.php is in the same directory

// Get the raw POST data
$data = file_get_contents("php://input");
$orderData = json_decode($data, true);

// Convert date strings to proper MySQL DATE format
$orderDate = date("Y-m-d", strtotime($orderData['OrderDate']));
$shippingDate = date("Y-m-d", strtotime($orderData['ShippingDate']));

// Prepare and bind
$stmt = $conn->prepare("INSERT INTO orders (CustomerID, CustomerName, UserID, ProductName, OrderDate, ShippingDate, Quantity, UnitPrice, TotalAmount, OrderStatus, Address) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

$stmt->bind_param("isisssiddss", 
    $orderData['CustomerID'], 
    $orderData['CustomerName'], 
    $orderData['UserID'], 
    $orderData['ProductName'], 
    $orderDate, // Bind as MySQL DATE type
    $shippingDate, // Bind as MySQL DATE type
    $orderData['Quantity'], 
    $orderData['UnitPrice'], 
    $orderData['TotalAmount'], 
    $orderData['OrderStatus'], 
    $orderData['Address']
);

// Execute and check if successful
if ($stmt->execute()) {
    echo json_encode(array("status" => "success"));
} else {
    echo json_encode(["message" => "Failed to place the order", "error" => $stmt->error]);
}

$stmt->close();
$conn->close();
?>
