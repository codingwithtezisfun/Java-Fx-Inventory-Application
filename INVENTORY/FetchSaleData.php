
<?php
require 'Connection.php';

$sql = "SELECT * FROM sales";
$result = $conn->query($sql);

$rows = array();
while($row = $result->fetch_assoc()) {
    $rows[] = $row;
}

header('Content-Type: application/json');
echo json_encode($rows);

$conn->close();
?>
