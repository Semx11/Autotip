package me.semx11.autotip.modcore;

import me.semx11.autotip.universal.UniversalUtil;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;

import java.io.File;
import java.util.List;

public class Tweaker implements ITweaker {

    private File gameDir = null;

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
        this.gameDir = gameDir;
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        if (System.getProperty("AUTOTIP_SKIP_MODCORE", "false").equalsIgnoreCase("true")) {
            System.out.println("Autotip not initializing ModCore because AUTOTIP_SKIP_MODCORE is true.");
            return;
        }
        //Minecraft Version
        int initialize = ModCoreInstaller.initialize(gameDir, UniversalUtil.getMinecraftVersion().toString()+"_forge");
        System.out.println("ModCore Init Status From ExampleMod " + initialize);
    }

    @Override
    public String getLaunchTarget() {
        return "net.minecraft.client.main.Main";
    }

    @Override
    public String[] getLaunchArguments() {
        return new String[0];
    }
}