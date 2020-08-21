package com.trip.ibu.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileUtils {
    public static FileInputStream openInputStream(File file) throws IOException {
        if (!file.exists()) {
            throw new FileNotFoundException("File '" + file + "' does not exist");
        }
        if (file.isDirectory()) {
            throw new IOException("File '" + file + "' exists but is a directory");
        }
        if (!file.canRead()) {
            throw new IOException("File '" + file + "' cannot be read");
        }
        return new FileInputStream(file);
    }
}