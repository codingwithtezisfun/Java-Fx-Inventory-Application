<?php
require 'Connection.php';

header('Content-Type: application/json');

$data = json_decode(file_get_contents('php://input'), true);

$productName = $data['ProductName'];
$quantityBought = $data['QuantityBought'];
$unitPrice = $data['UnitPrice'];
$totalAmount = $data['TotalAmount'];
$customerID = $data['CustomerID'];
$saleDate = date('Y-m-d'); // Assuming today's date for simplicity

$sql = "INSERT INTO sales (SaleDate, ProductName, QuantityBought, UnitPrice, TotalAmount, CustomerID) VALUES (?, ?, ?, ?, ?, ?)";
$stmt = $conn->prepare($sql);
$stmt->bind_param("ssiddi", $saleDate, $productName, $quantityBought, $unitPrice, $totalAmount, $customerID);

if ($stmt->execute()) {
    echo json_encode(["status" => "success"]);
} else {
    echo json_encode(["status" => "error", "message" => $stmt->error]);
}

$stmt->close();
$conn->close();
?>
