<?php
require 'Connection.php'; // Ensure this line is correct and Connection.php is in the same directory

$query = "SELECT id FROM users";
$result = $conn->query($query);

$userIds = [];

while ($row = $result->fetch_assoc()) {
    $userIds[] = $row['id'];
}

echo json_encode($userIds);
$conn->close();
?>
