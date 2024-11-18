package model;

import javafx.scene.image.Image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * ImageModel represents an image, encapsulating its file, buffered image,
 * thumbnail, and associated properties.
 * This class is designed to be immutable after initialization to ensure thread safety
 * and consistency of the image data throughout its lifecycle.
 */
public final class ImageModel {

    private final File file;
    private final BufferedImage bufferedImage;
    private final Image thumbnail;
    private final ImageProperties properties;

    /**
     * Constructs an ImageModel with the specified image file.
     *
     * @param file the image file to be represented by this model.
     * @throws IOException if the image cannot be read or has an unsupported format.
     * @throws IllegalArgumentException if the provided file is null or does not exist.
     */
    public ImageModel(File file) throws IOException {
        validateFile(file);
        this.file = file;
        this.bufferedImage = loadImageFromFile(file);
        this.thumbnail = createThumbnail(file);
        this.properties = extractProperties(file, bufferedImage);
    }

    /**
     * Validates the provided image file.
     *
     * @param file the image file to validate.
     * @throws IllegalArgumentException if the file is null, does not exist, or is not a file.
     */
    private void validateFile(File file) {
        if (file == null) {
            throw new IllegalArgumentException("Image file cannot be null.");
        }
        if (!file.exists()) {
            throw new IllegalArgumentException("Image file does not exist: " + file.getAbsolutePath());
        }
        if (!file.isFile()) {
            throw new IllegalArgumentException("Provided path is not a file: " + file.getAbsolutePath());
        }
    }

    /**
     * Loads the image from the specified file into a BufferedImage.
     *
     * @param file the image file to load.
     * @return the loaded BufferedImage.
     * @throws IOException if the image cannot be read or has an unsupported format.
     */
    private BufferedImage loadImageFromFile(File file) throws IOException {
        BufferedImage img = ImageIO.read(file);
        if (img == null) {
            throw new IOException("Unsupported image format or corrupted file: " + file.getName());
        }
        return img;
    }

    /**
     * Creates a thumbnail of the image for display purposes.
     * The thumbnail preserves the aspect ratio and fits within the specified dimensions.
     *
     * @param file the image file to create a thumbnail from.
     * @return the generated thumbnail as a JavaFX Image.
     */
    private Image createThumbnail(File file) {
        // Thumbnail dimensions
        final double THUMBNAIL_WIDTH = 100;
        final double THUMBNAIL_HEIGHT = 100;

        // Generate thumbnail using JavaFX 's Image class with aspect ratio preserved and smoothing enabled
        return new Image(file.toURI().toString(), THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT, true, true, true);
    }

    /**
     * Extracts properties from the image file and its buffered image.
     *
     * @param file the image file.
     * @param bufferedImage the buffered image of the file.
     * @return an ImageProperties object containing extracted metadata.
     */
    private ImageProperties extractProperties(File file, BufferedImage bufferedImage) {
        return new ImageProperties(file, bufferedImage);
    }

    /**
     * Retrieves the original image file.
     *
     * @return the image file.
     */
    public File getFile() {
        return file;
    }

    /**
     * Retrieves the buffered image representation.
     *
     * @return the BufferedImage.
     */
    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    /**
     * Retrieves the thumbnail image for display purposes.
     *
     * @return the thumbnail as a JavaFX Image.
     */
    public Image getThumbnail() {
        return thumbnail;
    }

    /**
     * Retrieves the properties associated with the image.
     *
     * @return the ImageProperties object containing metadata.
     */
    public ImageProperties getProperties() {
        return properties;
    }
}
