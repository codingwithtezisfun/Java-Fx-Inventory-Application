<?php
require 'Connection.php';

header('Content-Type: application/json');

$batchNumbers = array();

try {
    $sql = "SELECT BatchNumber FROM purchases";
    $result = $conn->query($sql);

    if ($result->num_rows > 0) {
        while ($row = $result->fetch_assoc()) {
            $batchNumbers[] = $row['BatchNumber'];
        }
    }

    $conn->close();
    echo json_encode($batchNumbers);
} catch (Exception $e) {
    echo json_encode(["error" => $e->getMessage()]);
}
?>
