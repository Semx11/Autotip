package me.semx11.autotip.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class NioWrapper {

    public static boolean exists(String path) {
        return exists(getPath(path));
    }

    public static boolean exists(Path path) {
        return Files.exists(path);
    }

    public static Path getPath(String path) {
        return Paths.get(separator(path));
    }

    public static File getFile(String path) {
        return getPath(path).toFile();
    }

    public static String separator(String s) {
        return s.replaceAll("///", File.separator);
    }

}
