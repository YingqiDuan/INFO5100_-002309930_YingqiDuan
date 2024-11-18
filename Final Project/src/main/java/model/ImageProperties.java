package model;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.*;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.GpsDirectory;
import com.drew.lang.GeoLocation;

import java.awt.image.BufferedImage;
import java.io.File;

/**
 * ImageProperties encapsulates metadata of an image, including dimensions,
 * file size, format, camera model, and geolocation (latitude and longitude).
 * This class is immutable and thread-safe.
 */
public final class ImageProperties {

    private final int width;
    private final int height;
    private final long fileSizeKB;
    private final String format;
    private final String cameraModel;
    private final String location;

    // Constants for default values
    private static final String DEFAULT_VALUE = "N/A";
    private static final String UNKNOWN_FORMAT = "UNKNOWN";

    /**
     * Constructs an ImageProperties instance by extracting metadata from the provided image file.
     *
     * @param file          The image file from which to extract properties.
     * @param bufferedImage The buffered image corresponding to the file.
     * @throws IllegalArgumentException if the file or bufferedImage is null.
     */
    public ImageProperties(File file, BufferedImage bufferedImage) {
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null.");
        }
        if (bufferedImage == null) {
            throw new IllegalArgumentException("BufferedImage cannot be null.");
        }

        this.width = bufferedImage.getWidth();
        this.height = bufferedImage.getHeight();
        this.fileSizeKB = file.length() / 1024;
        this.format = extractFileExtension(file);
        MetadataData metadataData = extractMetadata(file);

        this.cameraModel = metadataData.getCameraModel();
        this.location = metadataData.getLocation();
    }

    /**
     * Extracts the file extension and returns it in uppercase.
     *
     * @param file The image file.
     * @return The file extension in uppercase, or "UNKNOWN" if not found.
     */
    private String extractFileExtension(File file) {
        String name = file.getName();
        int lastDot = name.lastIndexOf('.');
        if (lastDot > 0 && lastDot < name.length() - 1) {
            return name.substring(lastDot + 1).toUpperCase();
        }
        return UNKNOWN_FORMAT;
    }

    /**
     * Extracts metadata such as camera model and GPS location from the image file.
     *
     * @param file The image file.
     * @return A MetadataData object containing extracted metadata.
     */
    private MetadataData extractMetadata(File file) {
        String cameraModel = DEFAULT_VALUE;
        String location = DEFAULT_VALUE;

        try {
            Metadata metadata = ImageMetadataReader.readMetadata(file);

            // Extract camera model from ExifIFD0Directory
            ExifIFD0Directory exifDirectory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
            if (exifDirectory != null) {
                String model = exifDirectory.getString(ExifIFD0Directory.TAG_MODEL);
                if (model != null && !model.trim().isEmpty()) {
                    cameraModel = model.trim();
                }
            }

            // Extract GPS data from GpsDirectory
            GpsDirectory gpsDirectory = metadata.getFirstDirectoryOfType(GpsDirectory.class);
            if (gpsDirectory != null) {
                GeoLocation geoLocation = gpsDirectory.getGeoLocation();
                if (geoLocation != null && !geoLocation.isZero()) {
                    location = String.format("Latitude: %.6f, Longitude: %.6f",
                            geoLocation.getLatitude(), geoLocation.getLongitude());
                }
            }
        } catch (Exception e) {
            // In a real-world scenario, consider logging the exception using a logging framework
            // For example: Logger.error("Failed to extract metadata from file: {}", file.getName(), e);
            // Here, we silently ignore and retain default "N/A" values
        }

        return new MetadataData(cameraModel, location);
    }

    /**
     * Retrieves the width of the image in pixels.
     *
     * @return Image width.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Retrieves the height of the image in pixels.
     *
     * @return Image height.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Retrieves the file size in kilobytes.
     *
     * @return File size in KB.
     */
    public long getFileSizeKB() {
        return fileSizeKB;
    }

    /**
     * Retrieves the image format (file extension).
     *
     * @return Image format.
     */
    public String getFormat() {
        return format;
    }

    /**
     * Retrieves the camera model used to capture the image.
     *
     * @return Camera model or "N/A" if not available.
     */
    public String getCameraModel() {
        return cameraModel;
    }

    /**
     * Retrieves the geolocation where the image was captured.
     *
     * @return Location in "Latitude: x, Longitude: y" format or "N/A" if not available.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Inner static class to hold extracted metadata.
     * This helps in organizing metadata extraction results.
     */
    private static class MetadataData {
        private final String cameraModel;
        private final String location;

        /**
         * Constructs MetadataData with specified camera model and location.
         *
         * @param cameraModel The camera model.
         * @param location    The geolocation.
         */
        public MetadataData(String cameraModel, String location) {
            this.cameraModel = cameraModel;
            this.location = location;
        }

        /**
         * Retrieves the camera model.
         *
         * @return Camera model.
         */
        public String getCameraModel() {
            return cameraModel;
        }

        /**
         * Retrieves the geolocation.
         *
         * @return Location.
         */
        public String getLocation() {
            return location;
        }
    }
}
