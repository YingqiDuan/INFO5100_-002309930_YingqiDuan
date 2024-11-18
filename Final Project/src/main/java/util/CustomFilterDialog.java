package util;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.util.Optional;

/**
 * CustomFilterDialog allows users to input custom convolution kernels for image filtering.
 */
public class CustomFilterDialog extends Dialog<float[]> {

    private static final int DEFAULT_KERNEL_SIZE = 3;

    public CustomFilterDialog() {
        setTitle("Custom Filter");
        setHeaderText("Enter Custom Convolution Kernel");

        // Set the button types
        ButtonType applyButtonType = new ButtonType("Apply", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(applyButtonType, ButtonType.CANCEL);

        // Create the grid layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Input for kernel size
        TextField sizeField = new TextField(String.valueOf(DEFAULT_KERNEL_SIZE));
        sizeField.setPromptText("Kernel Size (e.g., 3)");

        grid.add(new Label("Kernel Size:"), 0, 0);
        grid.add(sizeField, 1, 0);

        // Container for kernel value fields
        GridPane kernelGrid = new GridPane();
        kernelGrid.setHgap(5);
        kernelGrid.setVgap(5);
        kernelGrid.setPadding(new Insets(10, 0, 0, 0));

        grid.add(new Label("Kernel Values:"), 0, 1);
        grid.add(kernelGrid, 1, 1);

        // Listener to update kernel fields based on size input
        sizeField.textProperty().addListener((observable, oldValue, newValue) -> {
            kernelGrid.getChildren().clear();
            try {
                int size = Integer.parseInt(newValue);
                if (size <= 0 || size > 10) { // Arbitrary limits to prevent excessive input
                    return;
                }
                for (int row = 0; row < size; row++) {
                    for (int col = 0; col < size; col++) {
                        TextField tf = new TextField("0");
                        tf.setPrefWidth(50);
                        kernelGrid.add(tf, col, row);
                    }
                }
            } catch (NumberFormatException e) {
                // Invalid size input; do nothing
            }
        });

        // Trigger initial kernel fields
        sizeField.setText(String.valueOf(DEFAULT_KERNEL_SIZE));

        getDialogPane().setContent(grid);

        // Convert the result to a float array when the apply button is clicked
        setResultConverter(dialogButton -> {
            if (dialogButton == applyButtonType) {
                try {
                    int size = Integer.parseInt(sizeField.getText());
                    float[] kernel = new float[size * size];
                    for (int row = 0; row < size; row++) {
                        for (int col = 0; col < size; col++) {
                            TextField tf = (TextField) kernelGrid.getChildren().get(row * size + col);
                            kernel[row * size + col] = Float.parseFloat(tf.getText());
                        }
                    }
                    return kernel;
                } catch (Exception e) {
                    // Show error alert for invalid input
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid kernel values. Please ensure all fields are filled with valid numbers.", ButtonType.OK);
                    alert.setHeaderText(null);
                    alert.showAndWait();
                }
            }
            return null;
        });
    }
}
