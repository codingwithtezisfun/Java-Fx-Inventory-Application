<?php
require 'Connection.php';

header('Content-Type: application/json');  

$category = array();

try {
    $sql = "SELECT CategoryID, CategoryName FROM categories";
    $result = $conn->query($sql);

    if ($result->num_rows > 0) {
        while ($row = $result->fetch_assoc()) {
            $category[] = $row;
        }
    }
    
    $conn->close();
    echo json_encode($category);
} catch (Exception $e) {
    echo json_encode(["error" => $e->getMessage()]);
}
?>
