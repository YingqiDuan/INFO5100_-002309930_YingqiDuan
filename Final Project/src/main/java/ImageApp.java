import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * ImageApp serves as the main entry point for the Image Processing Application.
 */
public class ImageApp extends Application {

    // Logger instance for logging events and errors
    private static final Logger logger = LoggerFactory.getLogger(ImageApp.class);

    // Constants for configuration
    private static final String FXML_FILE_PATH = "/view/main.fxml";
    private static final int WINDOW_WIDTH = 1000;
    private static final int WINDOW_HEIGHT = 700;
    private static final String APPLICATION_TITLE = "JavaFX Image App";

    // Constants for minimum window size
    private static final int MIN_WIDTH = 400;
    private static final int MIN_HEIGHT = 600;

    /**
     * The main entry point for all JavaFX applications.
     *
     * @param primaryStage The primary stage for this application.
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            logger.info("Starting ImageApp...");

            // Load the FXML layout from the specified path
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_FILE_PATH));

            // Create a new scene with the loaded layout and set its dimensions
            Scene scene = new Scene(loader.load(), WINDOW_WIDTH, WINDOW_HEIGHT);

            // Configure the primary stage with title and scene
            primaryStage.setTitle(APPLICATION_TITLE);
            primaryStage.setScene(scene);

            // Set minimum window size to prevent UI components from disappearing
            primaryStage.setMinWidth(MIN_WIDTH);
            primaryStage.setMinHeight(MIN_HEIGHT);

            primaryStage.show();

            logger.info("ImageApp started successfully.");
        } catch (IOException e) {
            // Log the exception with an error level
            logger.error("Failed to load the FXML layout from path: {}", FXML_FILE_PATH, e);

            // Optionally, show an alert to inform the user about the error
            showErrorAlert("Application Error", "Failed to load the application layout.");
        }
    }

    /**
     * Launches the Image Processing Application.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Displays an error alert to the user.
     *
     * @param title   The title of the alert dialog.
     * @param message The error message to display.
     */
    private void showErrorAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
