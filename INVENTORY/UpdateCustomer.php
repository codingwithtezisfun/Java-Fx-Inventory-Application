<?php
require 'Connection.php';

header('Content-Type: application/json');

$data = json_decode(file_get_contents('php://input'), true);

$customerID = $data['CustomerID'];
$customerName = $data['CustomerName'];
$address = $data['Address'];
$phone = $data['Phone'];
$email = $data['Email'];

$sql = "UPDATE customers SET CustomerName='$customerName', Address='$address', Phone='$phone', Email='$email' WHERE CustomerID='$customerID'";

if ($conn->query($sql) === TRUE) {
    $response = array("status" => "success");
} else {
    $response = array("status" => "error", "message" => $conn->error);
}

$conn->close();
echo json_encode($response);
?>
