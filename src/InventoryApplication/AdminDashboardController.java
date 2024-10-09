package InventoryApplication;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

public class AdminDashboardController {

    @FXML
    private Button registerButton;

    @FXML
    private TextField username;

    @FXML
    private TextField email;

    @FXML
    private TextField password;

    @FXML
    private TextField confirmPass;

    @FXML
    private TextField deleteUsername;

    @FXML
    private Button deleteButton;

    @FXML
    private TextField oldUsername;

    @FXML
    private TextField newUsername;

    @FXML
    private TextField newPassword;

    @FXML
    private Button updateAdminButton;

    @FXML
    private TextField adminUsername;

    @FXML
    private TextField adminEmail;

    @FXML
    private TextField adminPassword;

    @FXML
    private TextField confirmAdminPass;

    DashboardDatabase dbHandler = new DashboardDatabase();

    @FXML
    void deleteUser(ActionEvent event) {
        String usernameToDelete = deleteUsername.getText();
        if (usernameToDelete != null && !usernameToDelete.isEmpty()) {
            dbHandler.deleteUser(usernameToDelete);
        }
    }

    @FXML
    void registerAdmin(ActionEvent event) {
        String name = adminUsername.getText();
        String email = adminEmail.getText();
        String password = adminPassword.getText();
        String confirmPassword = confirmAdminPass.getText();

        if (password.equals(confirmPassword)) {
            dbHandler.addUser(name, email, password);
        }
    }

    @FXML
    void saveUser(ActionEvent event) {
        String name = username.getText();
        String email = this.email.getText();
        String password = this.password.getText();
        String confirmPassword = confirmPass.getText();

        if (!password.isEmpty() && password.equals(confirmPassword)) {
            dbHandler.addUser(name, email, password);
            showAlert("SUCCESS","New user has been registered");
            username.setText("");
            this.email.setText("");
            this.password.setText("");
            confirmPass.setText("");
        }else{
        	showAlertError("Error","Password mismatch or there is an empty field");
        }

    }

    @FXML
    void updateAdmin(ActionEvent event) {
        String oldName = oldUsername.getText();
        String newName = newUsername.getText();
        String newPassword = this.newPassword.getText();

        dbHandler.updateUser(oldName, newName, newPassword);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void showAlertError(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
