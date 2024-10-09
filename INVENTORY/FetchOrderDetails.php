<?php
require 'Connection.php'; // Ensure this line is correct and Connection.php is in the same directory

if (isset($_GET['orderId'])) {
    $orderId = $_GET['orderId'];

    $query = "SELECT * FROM orders WHERE OrderID = ?";
    $stmt = $conn->prepare($query);
    $stmt->bind_param('i', $orderId);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($result->num_rows > 0) {
        $row = $result->fetch_assoc();
        echo json_encode($row);
    } else {
        echo json_encode(null); // Return null if order ID not found
    }

    $stmt->close();
} else {
    echo json_encode(null); // Return null if orderId parameter is not set
}

$conn->close();
?>
