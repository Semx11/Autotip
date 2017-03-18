package me.semx11.autotip.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Host {

    private String id;
    private String url;
    private boolean enabled;

    public Host(String id, String url, boolean enabled) {
        this.id = id;
        this.url = url;
        this.enabled = enabled;
    }

    public static void main(String[] args) {
        Host host = new Host("download", "autotip.sk1er.club", true);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(host);
        System.out.println(json);
    }

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public boolean isEnabled() {
        return enabled;
    }

}
