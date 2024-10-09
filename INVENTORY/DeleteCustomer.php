<?php
require 'Connection.php';

header('Content-Type: application/json');

$data = json_decode(file_get_contents('php://input'), true);

$customerID = $data['CustomerID'];

$sql = "DELETE FROM customers WHERE CustomerID='$customerID'";

if ($conn->query($sql) === TRUE) {
    $response = array("status" => "success");
} else {
    $response = array("status" => "error", "message" => $conn->error);
}

$conn->close();
echo json_encode($response);
?>
        