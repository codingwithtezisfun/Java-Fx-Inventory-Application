<?php

require 'Connection.php';

header('Content-Type: application/json');

// Decode JSON input from the client
$data = json_decode(file_get_contents('php://input'), true);

if (isset($data['batchNumber'])) {
    $batchNumber = $data['batchNumber'];

    try {
        // Prepare SQL statement
        $sql = "DELETE FROM purchases WHERE BatchNumber = :batchNumber";
        $stmt = $conn->prepare($sql);
        $stmt->bindParam(':batchNumber', $batchNumber, PDO::PARAM_STR);

        // Execute the query
        $stmt->execute();

        // Check if any rows were affected
        $rowCount = $stmt->rowCount();
        if ($rowCount > 0) {
            echo json_encode(['success' => true, 'message' => 'Product deleted successfully.']);
        } else {
            echo json_encode(['success' => false, 'message' => 'No product found with that batch number.']);
        }
    } catch (PDOException $e) {
        echo json_encode(['success' => false, 'message' => 'Database error: ' . $e->getMessage()]);
    }
} else {
    echo json_encode(['success' => false, 'message' => 'Batch number not provided.']);
}

$conn = null; // Close database connection
?>
