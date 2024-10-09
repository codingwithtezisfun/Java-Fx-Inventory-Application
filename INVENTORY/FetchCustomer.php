
<?php
require 'Connection.php';

header('Content-Type: application/json');

$sql = "SELECT * FROM customers";
$result = $conn->query($sql);

$customers = array();

if ($result->num_rows > 0) {
    while ($row = $result->fetch_assoc()) {
        $customers[] = $row;
    }
}

$conn->close();
echo json_encode($customers);
?>
