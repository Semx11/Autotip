package me.semx11.autotip.util;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.annotation.CheckReturnValue;
import me.semx11.autotip.Autotip;
import me.semx11.autotip.gson.Exclude;
import org.apache.commons.io.FileUtils;

public class Config {

    private static final Gson GSON = Autotip.getInstance().getGson();
    private static final FileUtil FILE_UTIL = Autotip.getInstance().getFileUtil();

    @Exclude
    private final Path configPath;
    @Exclude
    private final File configFile;

    private boolean enabled = true;
    private MessageOption messageOption = MessageOption.SHOWN;

    public Config(Autotip autotip) {
        this.configPath = autotip.getFileUtil().getPath("config.at");
        this.configFile = configPath.toFile();
    }

    public boolean isEnabled() {
        return enabled;
    }

    @CheckReturnValue
    public Config setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @CheckReturnValue
    public Config toggleEnabled() {
        this.enabled = !this.enabled;
        return this;
    }

    public MessageOption getMessageOption() {
        return messageOption;
    }

    @CheckReturnValue
    public Config nextMessageOption() {
        this.messageOption = messageOption.next();
        return this;
    }

    public Config save() {
        try {
            FileUtils.writeStringToFile(configFile, GSON.toJson(this), StandardCharsets.UTF_8);
        } catch (IOException e) {
            Autotip.LOGGER.error("Could not write config to " + configFile, e);
        }
        return this;
    }

    public Config load() {
        try {
            String json = FileUtils.readFileToString(configFile);
            return this.merge(GSON.fromJson(json, Config.class));
        } catch (FileNotFoundException e) {
            Autotip.LOGGER.info("config.at does not exist, creating...");
        } catch (JsonSyntaxException e) {
            Autotip.LOGGER.warn("config.at has invalid contents, resetting...");
        } catch (IOException e) {
            Autotip.LOGGER.error("Could not read config.at!", e);
        }
        return this.save();
    }

    public Config migrate() {
        // Check if legacy config file exists
        File legacyFile = FILE_UTIL.getFile("options.at");
        if (!legacyFile.exists()) {
            return this;
        }

        try {
            List<String> lines = Files.readAllLines(configPath.resolveSibling("options.at"));
            if (lines.size() < 2) {
                return this;
            }

            this.enabled = Boolean.parseBoolean(lines.get(0));
            try {
                this.messageOption = MessageOption.valueOf(lines.get(1));
            } catch (IllegalArgumentException | NullPointerException e) {
                this.messageOption = MessageOption.SHOWN;
            }

            // Deletes old file to complete migration
            if (!legacyFile.delete()) {
                Autotip.LOGGER.warn("Could not delete legacy options.at file!");
            }
            return this.save();
        } catch (IOException e) {
            Autotip.LOGGER.error("Could not read legacy options.at file!");
            return this.save();
        }
    }

    private Config merge(final Config that) {
        this.enabled = that.enabled;
        this.messageOption = that.messageOption;
        return this;
    }

}
