<?php
require 'Connection.php';
// Get the JSON input
$json = file_get_contents('php://input');
$data = json_decode($json, true);

// Extract data
$productName = $data['name'];
$unitPrice = $data['unitPrize'];
$quantityBought = $data['quantity'];
$batchNumber = $data['batchNo'];
$categoryName = $data['category'];
$supplierName = $data['supplier'];

// Find CategoryID
$categoryIdQuery = "SELECT CategoryID FROM categories WHERE CategoryName = ?";
$stmt = $conn->prepare($categoryIdQuery);
$stmt->bind_param("s", $categoryName);
$stmt->execute();
$result = $stmt->get_result();
$categoryId = $result->fetch_assoc()['CategoryID'];

// Find SupplierID
$supplierIdQuery = "SELECT SupplierID FROM suppliers WHERE SupplierName = ?";
$stmt = $conn->prepare($supplierIdQuery);
$stmt->bind_param("s", $supplierName);
$stmt->execute();
$result = $stmt->get_result();
$supplierId = $result->fetch_assoc()['SupplierID'];

// Insert the new purchase record
$insertQuery = "INSERT INTO purchases (ProductName, CategoryID, SupplierID, QuantityBought, UnitPrice, BatchNumber) VALUES (?, ?, ?, ?, ?, ?)";
$stmt = $conn->prepare($insertQuery);
$stmt->bind_param("siiids", $productName, $categoryId, $supplierId, $quantityBought, $unitPrice, $batchNumber);

if ($stmt->execute()) {
    echo json_encode(array("message" => "Product saved successfully"));
} else {
    echo json_encode(array("message" => "Error saving product"));
}

$stmt->close();
$conn->close();
?>
