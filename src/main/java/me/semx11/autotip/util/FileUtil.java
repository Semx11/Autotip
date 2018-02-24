package me.semx11.autotip.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import me.semx11.autotip.Autotip;

public class FileUtil {

    private final Path userDir;
    private final Path statsDir;

    public FileUtil(Autotip autotip) {
        this.userDir = this.getRawPath("mods/autotip/" + autotip.getGameProfile().getId());
        this.statsDir = this.getPath("stats");
    }

    public void createDirectories() throws IOException {
        if (!Files.exists(statsDir)) {
            Files.createDirectories(statsDir);
        }
    }

    public Path getUserDir() {
        return userDir;
    }

    public Path getStatsDir() {
        return statsDir;
    }

    public boolean exists(String path) {
        return Files.exists(this.getPath(path));
    }

    /*public boolean existsRaw(Path path) {
        return Files.exists(path);
    }*/

    public File getFile(String path) {
        return this.getPath(path).toFile();
    }

    public Path getPath(String path) {
        return this.userDir.resolve(this.separator(path));
    }

    private Path getRawPath(String path) {
        return Paths.get(this.separator(path));
    }

    private String separator(String s) {
        return s.replaceAll("///", File.separator);
    }

}