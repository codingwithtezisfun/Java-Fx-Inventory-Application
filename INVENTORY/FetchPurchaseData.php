<?php
require 'Connection.php';

$sql = "SELECT * FROM purchases";
$result = $conn->query($sql);

$rows = array();
while ($row = $result->fetch_assoc()) {
    $rows[] = $row;
}

echo json_encode($rows);

$conn->close();
?>
