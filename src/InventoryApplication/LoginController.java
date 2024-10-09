package InventoryApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private Button admin;

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

    @FXML
    void logUserIn(ActionEvent event) {
        String Username = username.getText();
        String Password = password.getText();
        validateUser(Username, Password);
    }

    private void validateUser(String Username, String Password) {
        HttpURLConnection connection = null;
        OutputStreamWriter writer = null;
        BufferedReader reader = null;
        try {
            String data = "username=" + URLEncoder.encode(Username, "UTF-8") + "&"
                        + "password=" + URLEncoder.encode(Password, "UTF-8");

            URL url = new URL("http://localhost/INVENTORY/FetchUser.php");
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(data);
            writer.flush();

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                Gson gson = new Gson();
                JsonObject jsonResponse = gson.fromJson(response.toString(), JsonObject.class);
                String status = jsonResponse.get("status").getAsString();

                if ("success".equals(status)) {

                    openMain(null);

                } else {
                     showAlert("Login failed", "Wrong password, try again.");
                }
            } else {
            	 showAlert("Error", "Failed to submit data: Response code " + responseCode + "ensure you are connected to the database");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) writer.close();
                if (reader != null) reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @FXML
    void openAdminDashboard(ActionEvent event) {
        try {
            root = FXMLLoader.load(getClass().getResource("AdminLoginWindow.fxml"));
            scene = new Scene(root);
            newStage = new Stage();
            newStage.setScene(scene);
            newStage.setTitle("Admin Login Window");
            newStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void openPasswordMenu(MouseEvent event) {
        // Implement password recovery functionality here
    }

    @FXML
    void openMain(MouseEvent event) {
    	try {
    		Stage currentStage = (Stage) login.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("Dashboard.fxml"));
            scene = new Scene(root);
            newStage = new Stage();
            newStage.setScene(scene);
            newStage.setTitle("Dashboard");
            newStage.show();
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
