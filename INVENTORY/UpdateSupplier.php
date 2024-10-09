<?php
require 'Connection.php';

header('Content-Type: application/json');

// Receive JSON input from POST request
$inputJSON = file_get_contents('php://input');
$input = json_decode($inputJSON, true);

if ($input) {
    // Extract data from JSON
    $supplierID = $input['SupplierID'];
    $email = $input['Email'];
    $phone = $input['Phone'];
    $address = $input['Address'];

    $sql = "UPDATE suppliers 
            SET Email='$email', Phone='$phone', Address='$address'
            WHERE SupplierID='$supplierID'";

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
