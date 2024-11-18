package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.HashSet;

/**
 * ImageConverter handles image format conversion using ImageIO.
 * It supports converting images to various formats such as PNG, JPEG, BMP, and GIF.
 */
public class ImageConverter implements ImageProcessor {

    private static final Logger logger = LoggerFactory.getLogger(ImageConverter.class);

    // Supported image formats for conversion
    private static final Set<String> SUPPORTED_FORMATS = new HashSet<>();
    static {
        SUPPORTED_FORMATS.add("png");
        SUPPORTED_FORMATS.add("jpeg");
        SUPPORTED_FORMATS.add("jpg");
        SUPPORTED_FORMATS.add("bmp");
        SUPPORTED_FORMATS.add("gif");
    }

    // Constants for default color when removing alpha channel
    private static final Color DEFAULT_BACKGROUND_COLOR = Color.WHITE;

    /**
     * Converts an image file to the specified format.
     *
     * @param inputFile  Source image file. Must not be null and must exist.
     * @param outputFile Destination image file. Must not be null and parent directory must exist.
     * @param format     Target format (e.g., "png", "jpeg"). Must be supported.
     * @throws IOException              If an error occurs during reading or writing.
     * @throws IllegalArgumentException If input parameters are invalid.
     */
    @Override
    public void process(File inputFile, File outputFile, String format) throws IOException {
        validateInputs(inputFile, outputFile, format);

        // Read the input image
        BufferedImage bufferedImage = ImageIO.read(inputFile);
        if (bufferedImage == null) {
            String errorMsg = "Unsupported or corrupted image format: " + inputFile.getName();
            logger.error(errorMsg);
            throw new IOException(errorMsg);
        }

        logger.debug("Successfully read image: {}", inputFile.getName());

        // Handle format-specific adjustments
        bufferedImage = adjustImageForFormat(bufferedImage, format, inputFile.getName());

        // Attempt to write the image in the target format
        boolean writeSuccess = ImageIO.write(bufferedImage, format.toLowerCase(), outputFile);
        if (!writeSuccess) {
            String errorMsg = "Failed to write image in format: " + format.toUpperCase();
            logger.error(errorMsg);
            throw new IOException(errorMsg);
        }

        logger.info("Successfully converted {} to {} format.", inputFile.getName(), format.toUpperCase());
    }

    /**
     * Validates the input parameters for the image conversion process.
     *
     * @param inputFile  Source image file.
     * @param outputFile Destination image file.
     * @param format     Target format.
     * @throws IllegalArgumentException If any input parameter is invalid.
     */
    private void validateInputs(File inputFile, File outputFile, String format) {
        if (inputFile == null) {
            throw new IllegalArgumentException("Input file cannot be null.");
        }
        if (!inputFile.exists()) {
            throw new IllegalArgumentException("Input file does not exist: " + inputFile.getAbsolutePath());
        }
        if (!inputFile.isFile()) {
            throw new IllegalArgumentException("Input path is not a file: " + inputFile.getAbsolutePath());
        }

        if (outputFile == null) {
            throw new IllegalArgumentException("Output file cannot be null.");
        }
        File parentDir = outputFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            boolean dirCreated = parentDir.mkdirs();
            if (!dirCreated) {
                throw new IllegalArgumentException("Unable to create output directory: " + parentDir.getAbsolutePath());
            }
        }

        if (format == null || format.trim().isEmpty()) {
            throw new IllegalArgumentException("Target format must be specified.");
        }
        String formatLower = format.toLowerCase();
        if (!SUPPORTED_FORMATS.contains(formatLower)) {
            throw new IllegalArgumentException("Unsupported target format: " + format);
        }

        logger.debug("Validated input parameters: inputFile={}, outputFile={}, format={}",
                inputFile.getName(), outputFile.getName(), format);
    }

    /**
     * Adjusts the image based on the target format requirements.
     * For example, JPEG does not support transparency, so the alpha channel is removed.
     *
     * @param image     The original BufferedImage.
     * @param format    The target format.
     * @param fileName  The name of the input file (for logging purposes).
     * @return The adjusted BufferedImage.
     */
    private BufferedImage adjustImageForFormat(BufferedImage image, String format, String fileName) {
        String formatLower = format.toLowerCase();

        if (formatLower.equals("jpg") || formatLower.equals("jpeg")) {
            if (image.getColorModel().hasAlpha()) {
                logger.info("Removing alpha channel from image: {}", fileName);
                BufferedImage rgbImage = new BufferedImage(
                        image.getWidth(),
                        image.getHeight(),
                        BufferedImage.TYPE_INT_RGB);

                // Draw the original image onto the new RGB image with a white background
                Graphics2D g2d = rgbImage.createGraphics();
                try {
                    g2d.setComposite(AlphaComposite.Src);
                    g2d.setColor(DEFAULT_BACKGROUND_COLOR);
                    g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
                    g2d.drawImage(image, 0, 0, null);
                } finally {
                    g2d.dispose();
                }

                return rgbImage;
            }
        }
        return image;
    }
}
