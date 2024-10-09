<?php
header('Content-Type: application/json');
require 'Connection.php';

// Receive JSON input from POST request
$inputJSON = file_get_contents('php://input');
$input = json_decode($inputJSON, true);

if ($input) {
    // Extract data from JSON
    $saleDate = $input['SaleDate'];
    $productName = $input['ProductName'];
    $quantityBought = $input['QuantityBought'];
    $unitPrice = $input['UnitPrice'];
    $totalAmount = $input['TotalAmount'];
    $customerID = $input['CustomerID'];

    $sql = "INSERT INTO sales (SaleDate, ProductName, QuantityBought, UnitPrice, TotalAmount, CustomerID) 
            VALUES ('$saleDate', '$productName', '$quantityBought', '$unitPrice', '$totalAmount', '$customerID')";

    if ($conn->query($sql) === TRUE) {
        echo json_encode(array("status" => "success"));
    } else {
        echo json_encode(array("status" => "error", "message" => $conn->error));
    }

    $conn->close();
} else {
    echo json_encode(array("status" => "error", "message" => "Invalid JSON input"));
}
?>
