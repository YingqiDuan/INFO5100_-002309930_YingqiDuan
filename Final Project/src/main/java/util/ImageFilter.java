package util;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

/**
 * ImageFilter provides utility methods to apply various filters to a BufferedImage.
 * Supported filters include Grayscale, Sepia, and Blur.
 * This class is non-instantiable and provides static methods for image processing.
 */
public final class ImageFilter {

    // Private constructor to prevent instantiation
    private ImageFilter() {
        throw new UnsupportedOperationException("ImageFilter is a utility class and cannot be instantiated.");
    }

    // Constants for color conversion
    private static final Color DEFAULT_BACKGROUND_COLOR = Color.WHITE;

    // Sepia tone coefficients
    private static final double SEPIA_RED_COEFF = 0.393;
    private static final double SEPIA_GREEN_COEFF = 0.769;
    private static final double SEPIA_BLUE_COEFF = 0.189;

    /**
     * Applies a grayscale filter to the provided BufferedImage.
     *
     * @param img Source BufferedImage. Must not be null.
     * @return A new BufferedImage after applying the grayscale filter.
     * @throws IllegalArgumentException if the input image is null.
     */
    public static BufferedImage applyGrayscale(BufferedImage img) {
        if (img == null) {
            throw new IllegalArgumentException("Input image cannot be null.");
        }

        // Create a grayscale image using ColorConvertOp for better performance
        BufferedImage grayscale = new BufferedImage(
                img.getWidth(),
                img.getHeight(),
                BufferedImage.TYPE_INT_ARGB);

        // Define a grayscale color space
        ColorConvertOp op = new ColorConvertOp(
                img.getColorModel().getColorSpace(),
                ColorSpace.getInstance(ColorSpace.CS_GRAY),
                null);

        // Perform the grayscale conversion
        op.filter(img, grayscale);

        return grayscale;
    }

    /**
     * Applies a sepia filter to the provided BufferedImage.
     *
     * @param img Source BufferedImage. Must not be null.
     * @return A new BufferedImage after applying the sepia filter.
     * @throws IllegalArgumentException if the input image is null.
     */
    public static BufferedImage applySepia(BufferedImage img) {
        if (img == null) {
            throw new IllegalArgumentException("Input image cannot be null.");
        }

        // Create a new image with the same dimensions and type
        BufferedImage sepia = new BufferedImage(
                img.getWidth(),
                img.getHeight(),
                BufferedImage.TYPE_INT_ARGB);

        // Iterate over each pixel to apply the sepia transformation
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int p = img.getRGB(x, y);

                // Extract color components
                int newPixel = getNewPixel(p);
                sepia.setRGB(x, y, newPixel);
            }
        }

        return sepia;
    }

    private static int getNewPixel(int p) {
        int alpha = (p >> 24) & 0xff;
        int red = (p >> 16) & 0xff;
        int green = (p >> 8) & 0xff;
        int blue = p & 0xff;

        // Apply sepia formula
        int tr = (int) (SEPIA_RED_COEFF * red + SEPIA_GREEN_COEFF * green + SEPIA_BLUE_COEFF * blue);
        int tg = (int) (0.349 * red + 0.686 * green + 0.168 * blue);
        int tb = (int) (0.272 * red + 0.534 * green + 0.131 * blue);

        // Clamp values to 255
        tr = Math.min(255, tr);
        tg = Math.min(255, tg);
        tb = Math.min(255, tb);

        // Reassemble pixel with original alpha and new color
        return alpha << 24 | (tr << 16) | (tg << 8) | tb;
    }

    /**
     * Applies a blur filter to the provided BufferedImage using a convolution kernel.
     *
     * @param img Source BufferedImage. Must not be null.
     * @return A new BufferedImage after applying the blur filter.
     * @throws IllegalArgumentException if the input image is null.
     */
    public static BufferedImage applyBlur(BufferedImage img) {
        if (img == null) {
            throw new IllegalArgumentException("Input image cannot be null.");
        }

        // Define a simple 3x3 blur kernel
        float[] matrix = {
                1f / 9f, 1f / 9f, 1f / 9f,
                1f / 9f, 1f / 9f, 1f / 9f,
                1f / 9f, 1f / 9f, 1f / 9f,
        };

        // Create a BufferedImage to hold the blurred image
        BufferedImage blurred = new BufferedImage(
                img.getWidth(),
                img.getHeight(),
                BufferedImage.TYPE_INT_ARGB);

        // Create a ConvolveOp with the blur kernel
        ConvolveOp op = new ConvolveOp(new Kernel(3, 3, matrix), ConvolveOp.EDGE_NO_OP, null);

        // Apply the convolution filter
        op.filter(img, blurred);

        return blurred;
    }

    /**
     * Applies a custom convolution filter to the provided BufferedImage.
     *
     * @param img    Source BufferedImage. Must not be null.
     * @param kernel A 1D array representing the convolution kernel. Length must be a perfect square.
     * @return A new BufferedImage after applying the convolution filter.
     * @throws IllegalArgumentException if the input image or kernel is null, or if the kernel size is invalid.
     */
    public static BufferedImage applyCustomFilter(BufferedImage img, float[] kernel) {
        if (img == null) {
            throw new IllegalArgumentException("Input image cannot be null.");
        }
        if (kernel == null || kernel.length == 0) {
            throw new IllegalArgumentException("Kernel cannot be null or empty.");
        }

        // Determine the kernel size (e.g., 3 for a 3x3 kernel)
        int size = (int) Math.sqrt(kernel.length);
        if (size * size != kernel.length) {
            throw new IllegalArgumentException("Kernel length must be a perfect square.");
        }

        // Create a new image to hold the filtered result
        BufferedImage filtered = new BufferedImage(
                img.getWidth(),
                img.getHeight(),
                BufferedImage.TYPE_INT_ARGB);

        // Create and apply the convolution operation
        ConvolveOp op = new ConvolveOp(new Kernel(size, size, kernel), ConvolveOp.EDGE_NO_OP, null);
        op.filter(img, filtered);

        return filtered;
    }
}
