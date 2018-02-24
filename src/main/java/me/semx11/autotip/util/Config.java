package me.semx11.autotip.util;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.annotation.CheckReturnValue;
import me.semx11.autotip.Autotip;
import me.semx11.autotip.gson.Exclude;
import org.apache.commons.io.FileUtils;

public class Config {

    private static final Gson GSON = Autotip.getInstance().getGson();

    @Exclude
    private final FileUtil fileUtil;
    @Exclude
    private final Path configPath;
    @Exclude
    private final File configFile;

    private boolean enabled;
    private MessageOption messageOption;

    public Config(Autotip autotip) {
        this.fileUtil = autotip.getFileUtil();
        this.configPath = fileUtil.getPath("config.at");
        this.configFile = configPath.toFile();

        // Default values
        this.enabled = true;
        this.messageOption = MessageOption.SHOWN;
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
            if (!Files.exists(configPath)) {
                Files.createFile(configPath);
            }
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
            Autotip.LOGGER.warn("config.at has invalid contents, resetting...", e);
        } catch (IOException e) {
            Autotip.LOGGER.error("Could not read config.at!", e);
        }
        return this.save();
    }

    private Config merge(Config that) {
        this.enabled = that.enabled;
        this.messageOption = that.messageOption;
        return this;
    }

}
