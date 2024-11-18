package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import model.ImageModel;
import model.ImageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.CustomFilterDialog;
import util.ImageConverter;
import util.ImageFilter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

/**
 * MainController handles user interactions in the Image Processing Application.
 * Implements object-oriented design using interfaces and inheritance.
 */
public class MainController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @FXML
    private FlowPane thumbnailsPane;

    @FXML
    private TextArea propertiesArea;

    @FXML
    private CheckBox pngCheckBox;

    @FXML
    private CheckBox jpegCheckBox;

    @FXML
    private CheckBox bmpCheckBox;

    @FXML
    private CheckBox gifCheckBox;

    @FXML
    private CheckBox grayscaleCheckBox;

    @FXML
    private CheckBox sepiaCheckBox;

    @FXML
    private CheckBox blurCheckBox;

    // Tracks the selected images using a Set to prevent duplicates
    private final Set<ImageModel> selectedImages = new HashSet<>();

    /**
     * Initializes the controller class. This method is automatically called after the FXML file has been loaded.
     */
    @FXML
    public void initialize() {
        logger.info("MainController initialized.");
    }

    /**
     * Handles the action of uploading images from the File menu.
     *
     * @param event The action event triggered by the user.
     */
    @FXML
    private void handleUploadImages(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image Files");

        // Set extension filters for supported image formats
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter(
                "Image Files", "*.png", "*.jpg", "*.jpeg", "*.bmp", "*.gif");
        fileChooser.getExtensionFilters().add(imageFilter);

        // Allow multiple file selection
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(thumbnailsPane.getScene().getWindow());

        if (selectedFiles != null && !selectedFiles.isEmpty()) {
            for (File file : selectedFiles) {
                try {
                    ImageModel imageModel = new ImageModel(file);
                    addThumbnail(imageModel);
                    logger.info("Image uploaded: {}", file.getName());
                } catch (Exception e) {
                    logger.error("Failed to load image: {}", file.getName(), e);
                    showAlert(Alert.AlertType.ERROR, "Upload Error", "Unable to load image: " + file.getName());
                }
            }
        } else {
            logger.info("No images selected for upload.");
        }
    }

    /**
     * Adds a thumbnail to the thumbnailsPane and maps it to its ImageModel.
     *
     * @param imageModel The ImageModel of the image.
     */
    private void addThumbnail(ImageModel imageModel) {
        ImageView imageView = new ImageView(imageModel.getThumbnail());
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setCache(true);

        // Wrap the ImageView in a StackPane to apply styles and effects
        StackPane imageContainer = new StackPane(imageView);
        imageContainer.setStyle("-fx-padding: 5;");

        // Set tooltip to display image name
        Tooltip tooltip = new Tooltip(imageModel.getFile().getName());
        Tooltip.install(imageView, tooltip);

        // Click event to select/deselect image
        imageContainer.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> toggleImageSelection(imageModel, imageContainer));

        thumbnailsPane.getChildren().add(imageContainer);
    }

    /**
     * Toggles the selection state of an image and updates the UI accordingly.
     *
     * @param imageModel     The ImageModel of the image.
     * @param imageContainer The container holding the image thumbnail.
     */
    private void toggleImageSelection(ImageModel imageModel, StackPane imageContainer) {
        if (selectedImages.contains(imageModel)) {
            selectedImages.remove(imageModel);
            imageContainer.setStyle("-fx-padding: 5;"); // Reset to default style
            logger.info("Image deselected: {}", imageModel.getFile().getName());
        } else {
            selectedImages.add(imageModel);
            imageContainer.setStyle("-fx-padding: 5; -fx-border-color: blue; -fx-border-width: 2;");
            logger.info("Image selected: {}", imageModel.getFile().getName());
        }

        updatePropertiesArea();
    }

    /**
     * Updates the propertiesArea based on the number of selected images.
     */
    private void updatePropertiesArea() {
        if (selectedImages.size() == 1) {
            ImageProperties props = selectedImages.iterator().next().getProperties();
            displayImageProperties(props);
        } else if (selectedImages.size() > 1) {
            propertiesArea.setText("Selected " + selectedImages.size() + " images.");
        } else {
            propertiesArea.clear();
        }
    }

    /**
     * Displays the properties of the selected image.
     *
     * @param properties The properties of the image.
     */
    private void displayImageProperties(ImageProperties properties) {
        StringBuilder sb = new StringBuilder();
        sb.append("Format: ").append(properties.getFormat()).append("\n");
        sb.append("Image Size: ").append(properties.getWidth()).append(" x ").append(properties.getHeight()).append(" px\n");
        sb.append("File Size: ").append(properties.getFileSizeKB()).append(" KB\n");
        sb.append("Camera Model: ").append(properties.getCameraModel()).append("\n");
        sb.append("Location: ").append(properties.getLocation()).append("\n");
        propertiesArea.setText(sb.toString());
    }

    /**
     * Handles the action of converting the selected images to different formats.
     *
     * @param event The action event triggered by the user.
     */
    @FXML
    private void handleConvertSelected(ActionEvent event) {
        if (selectedImages.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "No Image Selected", "Please select at least one image for conversion.");
            logger.warn("Conversion attempted with no images selected.");
            return;
        }

        List<String> targetFormats = getSelectedFormats();
        if (targetFormats.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "No Format Selected", "Please select at least one format for conversion.");
            logger.warn("Conversion attempted with no formats selected.");
            return;
        }

        File destinationDirectory = chooseDirectory("Select Save Directory for Converted Images");
        if (destinationDirectory == null) {
            logger.info("Conversion canceled by user.");
            return;
        }

        String destinationPath = destinationDirectory.getAbsolutePath();
        ImageConverter converter = new ImageConverter();

        int successCount = 0;
        int failureCount = 0;
        List<String> failedImages = new ArrayList<>();

        for (ImageModel imageModel : selectedImages) {
            for (String format : targetFormats) {
                try {
                    File inputFile = imageModel.getFile();
                    String baseName = getBaseName(inputFile.getName());
                    File outputFile = new File(destinationPath, baseName + "_converted." + format.toLowerCase());
                    converter.process(inputFile, outputFile, format.toLowerCase());
                    logger.info("Image converted: {} to {}", inputFile.getName(), format);
                    successCount++;
                } catch (IOException e) {
                    logger.error("Failed to convert image: {}", imageModel.getFile().getName(), e);
                    failedImages.add(imageModel.getFile().getName() + " to " + format.toUpperCase());
                    failureCount++;
                }
            }
        }

        // Display a summary message
        StringBuilder message = new StringBuilder();
        message.append("Conversion completed.\n");
        message.append("Successful conversions: ").append(successCount).append("\n");
        if (failureCount > 0) {
            message.append("Failed conversions: ").append(failureCount).append("\n");
            message.append("Failed to convert:\n");
            for (String failedImage : failedImages) {
                message.append("- ").append(failedImage).append("\n");
            }
            showAlert(Alert.AlertType.WARNING, "Conversion Completed with Errors", message.toString());
            logger.warn("Image conversion completed with some errors.");
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Conversion Complete", "Selected images have been successfully converted.");
            logger.info("Image conversion completed successfully.");
        }
    }

    /**
     * Retrieves the list of selected target formats for conversion.
     *
     * @return A list of selected formats.
     */
    private List<String> getSelectedFormats() {
        List<String> formats = new ArrayList<>();
        if (pngCheckBox.isSelected()) formats.add("PNG");
        if (jpegCheckBox.isSelected()) formats.add("JPEG");
        if (bmpCheckBox.isSelected()) formats.add("BMP");
        if (gifCheckBox.isSelected()) formats.add("GIF");
        return formats;
    }

    /**
     * Handles the action of applying custom filters to the selected images.
     *
     * @param event The action event triggered by the user.
     */
    @FXML
    private void handleApplyCustomFilter(ActionEvent event) {
        if (selectedImages.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "No Image Selected", "Please select at least one image to apply a custom filter.");
            logger.warn("Custom filter application attempted with no images selected.");
            return;
        }

        // Prompt user to enter kernel size and values
        CustomFilterDialog dialog = new CustomFilterDialog();
        Optional<float[]> result = dialog.showAndWait();

        result.ifPresent(kernel -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select Save Directory for Custom Filtered Images");
            File selectedDirectory = directoryChooser.showDialog(thumbnailsPane.getScene().getWindow());

            if (selectedDirectory != null) {
                String destinationPath = selectedDirectory.getAbsolutePath();
                int successCount = 0;
                int failureCount = 0;
                List<String> failedImages = new ArrayList<>();

                for (ImageModel imageModel : selectedImages) {
                    try {
                        BufferedImage originalImage = imageModel.getBufferedImage();
                        BufferedImage filteredImage = ImageFilter.applyCustomFilter(originalImage, kernel);

                        String format = imageModel.getProperties().getFormat().toLowerCase();
                        String baseName = getBaseName(imageModel.getFile().getName());
                        File outputFile = new File(destinationPath, baseName + "_custom_filtered." + format);

                        ImageIO.write(filteredImage, format, outputFile);
                        logger.info("Custom filtered image saved: {}", outputFile.getName());
                        successCount++;
                    } catch (Exception e) {
                        logger.error("Failed to apply custom filter to image: {}", imageModel.getFile().getName(), e);
                        failedImages.add(imageModel.getFile().getName());
                        failureCount++;
                    }
                }

                // Display a summary message
                StringBuilder message = new StringBuilder();
                message.append("Custom filter application completed.\n");
                message.append("Successfully filtered images: ").append(successCount).append("\n");
                if (failureCount > 0) {
                    message.append("Failed to filter images: ").append(failureCount).append("\n");
                    message.append("Failed to apply filter to:\n");
                    for (String failedImage : failedImages) {
                        message.append("- ").append(failedImage).append("\n");
                    }
                    showAlert(Alert.AlertType.WARNING, "Custom Filter Completed with Errors", message.toString());
                    logger.warn("Custom filter application completed with some errors.");
                } else {
                    showAlert(Alert.AlertType.INFORMATION, "Custom Filter Applied", "Selected images have been successfully filtered with the custom kernel.");
                    logger.info("Custom filter application completed successfully.");
                }
            } else {
                logger.info("Custom filter application canceled by user.");
            }
        });
    }

    /**
     * Retrieves the base name of a file (without extension).
     *
     * @param fileName The full file name.
     * @return The base name of the file.
     */
    private String getBaseName(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot > 0 && lastDot < fileName.length() - 1) {
            return fileName.substring(0, lastDot);
        }
        return fileName;
    }

    /**
     * Handles the action of downloading the selected images.
     *
     * @param event The action event triggered by the user.
     */
    @FXML
    private void handleDownloadSelected(ActionEvent event) {
        if (selectedImages.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "No Image Selected", "Please select at least one image for download.");
            logger.warn("Download attempted with no images selected.");
            return;
        }

        File destinationDirectory = chooseDirectory("Select Save Directory for Downloaded Images");
        if (destinationDirectory == null) {
            logger.info("Download canceled by user.");
            return;
        }

        String destinationPath = destinationDirectory.getAbsolutePath();
        int successCount = 0;
        int failureCount = 0;
        List<String> failedDownloads = new ArrayList<>();

        for (ImageModel imageModel : selectedImages) {
            try {
                File inputFile = imageModel.getFile();
                String baseName = getBaseName(inputFile.getName());
                String format = imageModel.getProperties().getFormat().toLowerCase();
                File outputFile = new File(destinationPath, baseName + "_downloaded." + format);
                Files.copy(inputFile.toPath(), outputFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                logger.info("Image downloaded: {}", inputFile.getName());
                successCount++;
            } catch (IOException e) {
                logger.error("Failed to download image: {}", imageModel.getFile().getName(), e);
                failedDownloads.add(imageModel.getFile().getName());
                failureCount++;
            }
        }

        // Display a summary message
        StringBuilder message = new StringBuilder();
        message.append("Download completed.\n");
        message.append("Successful downloads: ").append(successCount).append("\n");
        if (failureCount > 0) {
            message.append("Failed downloads: ").append(failureCount).append("\n");
            message.append("Failed to download:\n");
            for (String failedImage : failedDownloads) {
                message.append("- ").append(failedImage).append("\n");
            }
            showAlert(Alert.AlertType.WARNING, "Download Completed with Errors", message.toString());
            logger.warn("Image download completed with some errors.");
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Download Complete", "Selected images have been successfully downloaded.");
            logger.info("Image download completed successfully.");
        }
    }

    /**
     * Handles the action of applying filters to the selected images.
     *
     * @param event The action event triggered by the user.
     */
    @FXML
    private void handleApplyFilters(ActionEvent event) {
        if (selectedImages.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "No Image Selected", "Please select at least one image to apply filters.");
            logger.warn("Filter application attempted with no images selected.");
            return;
        }

        boolean grayscale = grayscaleCheckBox.isSelected();
        boolean sepia = sepiaCheckBox.isSelected();
        boolean blur = blurCheckBox.isSelected();

        if (!grayscale && !sepia && !blur) {
            showAlert(Alert.AlertType.WARNING, "No Filter Selected", "Please select at least one filter.");
            logger.warn("Filter application attempted with no filters selected.");
            return;
        }

        File destinationDirectory = chooseDirectory("Select Save Directory for Filtered Images");
        if (destinationDirectory == null) {
            logger.info("Filter application canceled by user.");
            return;
        }

        String destinationPath = destinationDirectory.getAbsolutePath();
        int successCount = 0;
        int failureCount = 0;
        List<String> failedFilters = new ArrayList<>();

        for (ImageModel imageModel : selectedImages) {
            try {
                BufferedImage processedImage = imageModel.getBufferedImage();

                // Apply selected filters
                if (grayscale) {
                    processedImage = ImageFilter.applyGrayscale(processedImage);
                    logger.info("Grayscale filter applied to: {}", imageModel.getFile().getName());
                }
                if (sepia) {
                    processedImage = ImageFilter.applySepia(processedImage);
                    logger.info("Sepia filter applied to: {}", imageModel.getFile().getName());
                }
                if (blur) {
                    processedImage = ImageFilter.applyBlur(processedImage);
                    logger.info("Blur filter applied to: {}", imageModel.getFile().getName());
                }

                String format = imageModel.getProperties().getFormat().toLowerCase();
                String baseName = getBaseName(imageModel.getFile().getName());
                File outputFile = new File(destinationPath, baseName + "_filtered." + format);
                ImageIO.write(processedImage, format, outputFile);
                logger.info("Filtered image saved: {}", outputFile.getName());
                successCount++;
            } catch (Exception e) {
                logger.error("Failed to apply filters to image: {}", imageModel.getFile().getName(), e);
                failedFilters.add(imageModel.getFile().getName());
                failureCount++;
            }
        }

        // Display a summary message
        StringBuilder message = new StringBuilder();
        message.append("Filter application completed.\n");
        message.append("Successfully filtered images: ").append(successCount).append("\n");
        if (failureCount > 0) {
            message.append("Failed to filter images: ").append(failureCount).append("\n");
            message.append("Failed to apply filters to:\n");
            for (String failedImage : failedFilters) {
                message.append("- ").append(failedImage).append("\n");
            }
            showAlert(Alert.AlertType.WARNING, "Filter Application Completed with Errors", message.toString());
            logger.warn("Filter application completed with some errors.");
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Filters Applied", "Selected images have been successfully filtered.");
            logger.info("Filter application completed successfully.");
        }
    }

    /**
     * Handles the action of exiting the application from the File menu.
     *
     * @param event The action event triggered by the user.
     */
    @FXML
    private void handleExit(ActionEvent event) {
        logger.info("Application exit initiated by user.");
        System.exit(0);
    }

    /**
     * Handles the action of displaying the About dialog from the Help menu.
     *
     * @param event The action event triggered by the user.
     */
    @FXML
    private void handleAbout(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "About", "Image Processing Application\nDeveloped using JavaFX.");
        logger.info("About dialog displayed to user.");
    }

    /**
     * Displays an alert dialog to the user.
     *
     * @param alertType The type of alert.
     * @param title     The title of the alert dialog.
     * @param message   The message content of the alert.
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        logger.debug("Displaying alert - Type: {}, Title: {}, Message: {}", alertType, title, message);
        Alert alert = new Alert(alertType, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    /**
     * Opens a DirectoryChooser dialog for the user to select a directory.
     *
     * @param title The title of the directory chooser dialog.
     * @return The selected directory as a File object, or null if no directory was selected.
     */
    private File chooseDirectory(String title) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(title);
        File selectedDirectory = directoryChooser.showDialog(thumbnailsPane.getScene().getWindow());

        if (selectedDirectory != null) {
            logger.info("Directory selected: {}", selectedDirectory.getAbsolutePath());
        } else {
            logger.info("No directory selected for {}", title.toLowerCase());
        }

        return selectedDirectory;
    }
}
