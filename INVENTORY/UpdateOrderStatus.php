<?php
require 'Connection.php'; 

// Check if the request method is POST
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    // Get input JSON data and decode it
    $inputData = json_decode(file_get_contents('php://input'), true);

    // Check if orderId and newStatus are set
    if (isset($inputData['orderId']) && isset($inputData['newStatus'])) {
        $orderId = $inputData['orderId'];
        $newStatus = $inputData['newStatus'];

        // Prepare and execute the SQL update statement
        $query = "UPDATE orders SET OrderStatus = ? WHERE OrderID = ?";
        $stmt = $conn->prepare($query);
        $stmt->bind_param('si', $newStatus, $orderId);
        $stmt->execute();

        // Check if the update was successful
        if ($stmt->affected_rows > 0) {
            echo json_encode(array('status' => 'success', 'message' => 'Order status updated successfully.'));
        } else {
            echo json_encode(array('status' => 'error', 'message' => 'Failed to update order status.'));
        }

        // Close the statement
        $stmt->close();
    } else {
        // Return error message if orderId or newStatus parameters are missing
        echo json_encode(array('status' => 'error', 'message' => 'Missing orderId or newStatus parameter.'));
    }
} else {
    // Return error message if the request method is not POST
    echo json_encode(array('status' => 'error', 'message' => 'Invalid request method.'));
}

// Close the database connection
$conn->close();
?>
