<?php
// Include your database connection file
require 'Connection.php'; // Ensure this line is correct and Connection.php is in the same directory

// Initialize an empty array to store order IDs
$orderIds = [];

// SQL query to select OrderID from the orders table
$query = "SELECT OrderID FROM orders";

// Execute the query
$result = $conn->query($query);

// Check if query execution was successful
if ($result) {
    // Fetch associative array of order IDs
    while ($row = $result->fetch_assoc()) {
        $orderIds[] = $row['OrderID'];
    }

    // Free result set
    $result->free();
} else {
    // If query execution failed, return an error response
    http_response_code(500); // Internal Server Error
    echo json_encode(array('status' => 'error', 'message' => 'Failed to fetch order IDs.'));
    exit;
}



header('Content-Type: application/json');
echo json_encode($orderIds);
// Close database connection
$conn->close();
?>
