package InventoryApplication;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SplashScreen extends Application {

    @FXML
    private ProgressBar progressBar;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("SplashScreen.fxml"));
        BorderPane root = loader.load();

        // Remove window decorations
        primaryStage.initStyle(StageStyle.UNDECORATED);

        // Create the scene and show the stage
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Access the controller
        SplashScreen controller = loader.getController();
        controller.initializeProgressBar(primaryStage);
    }

    public void initializeProgressBar(Stage primaryStage) {
        // Create a Task to update the progress bar
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                for (int i = 0; i <= 100; i++) {
                    updateProgress(i, 100);
                    Thread.sleep(50); // Delay
                }
                return null;
            }
        };

        // Bind the progress bar's progress property to the task's progress property
        progressBar.progressProperty().bind(task.progressProperty());

     // Handle task completion
        task.setOnSucceeded(event -> {
            try {
                // Load the Dashboard.fxml file
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
                BorderPane dashboardRoot = loader.load();

                // Create a new scene with the dashboard
                Scene dashboardScene = new Scene(dashboardRoot);

                // Create a new stage for the dashboard
                Stage dashboardStage = new Stage();
                dashboardStage.setTitle("Dashboard");
                dashboardStage.setScene(dashboardScene);

                // Show the new dashboard stage
                dashboardStage.show();

                // Close the splash screen stage
                primaryStage.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


        // Start the task in a new thread
        new Thread(task).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
