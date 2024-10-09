<?php
require 'Connection.php';

$sql = "SELECT ProductName, SUM(QuantityBought * UnitPrice) as TotalSpending
        FROM purchases
        GROUP BY ProductName";

$result = $conn->query($sql);

if ($result->num_rows > 0) {
    $totalSpendings = array();

    while ($row = $result->fetch_assoc()) {
        $totalSpending = array(
            'ProductName' => $row['ProductName'],
            'TotalSpending' => $row['TotalSpending']
        );
        $totalSpendings[] = $totalSpending;
    }

    $conn->close();

    header('Content-Type: application/json');

    echo json_encode($totalSpendings);
} else {
    echo "0 results";
}

?>
