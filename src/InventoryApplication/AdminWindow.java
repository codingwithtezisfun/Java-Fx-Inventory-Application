package InventoryApplication;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class AdminWindow {

    @FXML
    private Button login;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private Label forgotpassword;
    private Parent root;
    private Scene scene;
    private Stage newStage;

    private String Adminpassword = "7864";
    private String AdminUsername = "admin";

    @FXML
    void logUserIn(ActionEvent event) {
    	String enteredUsername = username.getText();
    	String enteredPassword = password.getText();
    	if(enteredUsername.equals(AdminUsername)&& enteredPassword.equals(Adminpassword)){
    	try{
    		Stage currentStage = (Stage) login.getScene().getWindow();
        root = FXMLLoader.load(getClass().getResource("AdminDashboard.fxml"));
        scene = new Scene(root);
        newStage = new Stage();
        newStage.setScene(scene);
        newStage.setTitle("Admin Dashboard");
        newStage.show();
        currentStage.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
    }else{
    	showAlertError("ERROR","Check credentials and try again");
    }
    }

    private void showAlertError(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}
