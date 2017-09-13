package me.semx11.autotip.util;

import com.google.gson.Gson;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import org.apache.commons.io.IOUtils;

public class Hosts {

    private static final Gson GSON = new Gson();

    private static Hosts instance;

    private List<Host> hosts;

    private Hosts() {
    }

    public static Hosts getInstance() {
        if (instance == null) {
            updateHosts();
        }
        return instance;
    }

    public static void updateHosts() {
        try {
            String json = IOUtils.toString(new URL("https://gist.githubusercontent.com/Semx11"
                    + "/35d6b58783ef8d0527f82782f6555834/raw/hosts.json"));
            instance = GSON.fromJson(json, Hosts.class);
        } catch (IOException e) {
            ErrorReport.reportException(e);
        }
    }

    public Host getHostById(String id) {
        return hosts.stream()
                .filter(h -> h.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

}
