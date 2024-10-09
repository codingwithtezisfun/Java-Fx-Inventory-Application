<?php
require 'Connection.php';

header('Content-Type: application/json');

$sql = "SELECT DISTINCT ProductName FROM purchases";
$result = $conn->query($sql);

$products = array();

if ($result->num_rows > 0) {
    while ($row = $result->fetch_assoc()) {
        $products[] = $row['ProductName'];
    }
}

$conn->close();
echo json_encode($products);
?>
