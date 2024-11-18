package util;

import java.io.File;
import java.io.IOException;

/**
 * ImageProcessor interface defines the basic operations for image processing.
 */
interface ImageProcessor {
    /**
     * Processes the image.
     *
     * @param inputFile  Source image file.
     * @param outputFile Destination image file.
     * @param format     Target format.
     * @throws IOException If an error occurs during processing.
     */
    void process(File inputFile, File outputFile, String format) throws IOException;
}
