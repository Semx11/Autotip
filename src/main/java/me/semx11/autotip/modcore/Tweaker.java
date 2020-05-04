package me.semx11.autotip.modcore;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.relauncher.CoreModManager;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
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
        try {
            String ver = ((String) ForgeVersion.class.getDeclaredField("mcVersion").get(null));
            //Minecraft Version
            int initialize = ModCoreInstaller.initialize(gameDir, ver + "_forge");
            System.out.println("ModCore Init Status From ExampleMod " + initialize);

        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        CodeSource codeSource = getClass().getProtectionDomain().getCodeSource();
        if (codeSource != null) {
            URL location = codeSource.getLocation();
            try {
                File file = new File(location.toURI());
                if (file.isFile()) {
                    CoreModManager.getIgnoredMods().remove(file.getName());
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No CodeSource, if this is not a development environment we might run into problems!");
            System.out.println(getClass().getProtectionDomain());
        }
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