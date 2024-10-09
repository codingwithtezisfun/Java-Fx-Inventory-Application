<?php
require 'Connection.php';


// SQL query to fetch sales data
$sql = "SELECT ProductName, SUM(TotalAmount) AS TotalSales FROM sales GROUP BY ProductName ORDER BY TotalSales DESC";

$result = $conn->query($sql);

// Prepare JSON array to hold sales data
$sales_data = array();

if ($result->num_rows > 0) {
    // Fetch rows from the result set
    while($row = $result->fetch_assoc()) {
        // Append each row as an array to $sales_data
        $sales_data[] = $row;
    }
} else {
    echo "0 results";
}

// Close connection
$conn->close();

// Output JSON formatted data
header('Content-Type: application/json');
echo json_encode($sales_data);
?>
