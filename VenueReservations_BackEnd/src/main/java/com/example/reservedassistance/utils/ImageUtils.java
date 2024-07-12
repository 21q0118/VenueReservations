package com.example.reservedassistance.utils;

public class ImageUtils {


    private enum ImageFormat {
        JPEG, PNG, GIF, BMP, WEBP, SVG, JPG;

        public static boolean isSupported(String extension) {
            for (ImageFormat format : values()) {
                if (format.name().equalsIgnoreCase(extension)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static boolean isImage(String filename) {
        String extension = getExtension(filename);
        return ImageFormat.isSupported(extension);
    }

    private static String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1) ? "" : filename.substring(dotIndex + 1);
    }




}
