<?php
require 'Connection.php';

header('Content-Type: application/json');

$data = json_decode(file_get_contents('php://input'), true);

$customerName = $data['CustomerName'];
$address = $data['Address'];
$phone = $data['Phone'];
$email = $data['Email'];

$sql = "INSERT INTO customers (CustomerName, Address, Phone, Email) VALUES ('$customerName', '$address', '$phone', '$email')";

if ($conn->query($sql) === TRUE) {
    $response = array("status" => "success", "CustomerID" => $conn->insert_id);
} else {
    $response = array("status" => "error", "message" => $conn->error);
}

$conn->close();
echo json_encode($response);
?>
