package com.vesoft.nebula.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NativeUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(NativeUtils.class.getName());
    private static final String NATIVE_FOLDER_PATH_PREFIX = "nativeutils";

    private static File temporaryDir;


    public static void loadLibraryFromJar(String path, Class<?> loadClass) throws IOException {
        if (null == path || !path.startsWith("/")) {
            throw new IllegalArgumentException("The path has to be absolute (start with '/').");
        }

        String[] parts = path.split("/");
        String filename = (parts.length > 1) ? parts[parts.length - 1] : null;

        if (filename == null) {
            throw new IllegalArgumentException("Invalid library path");
        }

        if (temporaryDir == null) {
            temporaryDir = createTempDirectory(NATIVE_FOLDER_PATH_PREFIX);
            temporaryDir.deleteOnExit();
        }

        File temp = new File(temporaryDir, filename);
        Class<?> clazz = loadClass == null ? NativeUtils.class : loadClass;

        InputStream is = null;
        try {
            is = clazz.getResourceAsStream(path);
            Files.copy(is, temp.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            temp.delete();
            is.close();
            throw e;
        } catch (NullPointerException e) {
            temp.delete();
            is.close();
            throw new FileNotFoundException("File " + path + " was not found inside JAR.");
        } finally {
            is.close();
        }

        try {
            System.load(temp.getAbsolutePath());
            LOGGER.info("Load " + temp.getAbsolutePath() + " as " + filename);
        } finally {
            temp.deleteOnExit();
        }
    }

    private static File createTempDirectory(String prefix) throws IOException {
        String tempDir = System.getProperty("java.io.tmpdir");
        File generatedDir = new File(tempDir, prefix + System.nanoTime());

        if (!generatedDir.mkdir()) {
            throw new IOException("Failed to create temp directory " + generatedDir.getName());
        }

        return generatedDir;
    }
}

