package me.semx11.autotip.util;

import com.google.gson.Gson;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import me.semx11.autotip.Autotip;
import me.semx11.autotip.util.VersionInfo.Severity;
import org.apache.commons.io.IOUtils;

public class Versions {

    private static final Gson GSON = new Gson();

    private static Versions instance;

    private Version latest;
    private Version latestBeta;
    private List<VersionInfo> versions = new ArrayList<>();

    private Versions() {
    }

    public static Versions getInstance() {
        if (instance == null) {
            updateVersions();
        }
        return instance;
    }

    public static void updateVersions() {
        try {
            String json = IOUtils.toString(new URL("https://gist.githubusercontent.com/Semx11"
                    + "/35d6b58783ef8d0527f82782f6555834/raw/versions.json"));
            instance = GSON.fromJson(json, Versions.class);
            instance.versions.sort((v1, v2) -> v2.getVersion().compareTo(v1.getVersion()));
        } catch (IOException e) {
            ErrorReport.reportException(e);
        }
    }

    public VersionInfo getInfoByVersion(Version version) {
        return versions.stream()
                .filter(v -> v.getVersion().equals(version))
                .findFirst()
                .orElse(new VersionInfo(version, Severity.OPTIONAL, true, "&cVersion not found."));
    }

    public List<VersionInfo> getHigherVersionInfo(Version version) {
        return getHigherVersionInfo(version, Autotip.BETA ? this.latestBeta : this.latest);
    }

    public List<VersionInfo> getHigherVersionInfo(Version version, Version highest) {
        return versions.stream()
                .filter(info -> Autotip.BETA || !info.isBetaVersion())
                .filter(info -> info.getVersion().compareTo(version) > 0
                        && info.getVersion().compareTo(highest) < 1)
                .collect(Collectors.toList());
    }

    public Version getLatest() {
        return latest;
    }

    public Version getLatestBeta() {
        return latestBeta;
    }

}
