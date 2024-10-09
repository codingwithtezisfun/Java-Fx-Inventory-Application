<?php
require 'Connection.php';

header('Content-Type: application/json');  

$suppliers = array();

try {
    $sql = "SELECT * FROM Suppliers";
    $result = $conn->query($sql);

    if ($result->num_rows > 0) {
        while ($row = $result->fetch_assoc()) {
            $suppliers[] = $row;
        }
    }
    
    $conn->close();
    echo json_encode($suppliers);
} catch (Exception $e) {
    echo json_encode(["error" => $e->getMessage()]);
}
?>
