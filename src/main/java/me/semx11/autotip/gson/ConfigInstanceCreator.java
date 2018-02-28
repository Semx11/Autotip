package me.semx11.autotip.gson;

import com.google.gson.InstanceCreator;
import java.lang.reflect.Type;
import me.semx11.autotip.Autotip;
import me.semx11.autotip.util.Config;

public class ConfigInstanceCreator implements InstanceCreator<Config> {

    private final Autotip autotip;

    public ConfigInstanceCreator(Autotip autotip) {
        this.autotip = autotip;
    }

    @Override
    public Config createInstance(Type type) {
        return new Config(autotip);
    }

}
