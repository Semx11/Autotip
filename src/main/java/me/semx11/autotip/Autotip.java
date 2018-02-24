package me.semx11.autotip;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.GameProfile;
import java.io.IOException;
import java.util.Arrays;
import me.semx11.autotip.command.CommandAbstract;
import me.semx11.autotip.command.impl.CommandAutotip;
import me.semx11.autotip.command.impl.CommandLimbo;
import me.semx11.autotip.command.impl.CommandTipHistory;
import me.semx11.autotip.core.SessionManager;
import me.semx11.autotip.core.TaskManager;
import me.semx11.autotip.event.EventChatReceived;
import me.semx11.autotip.event.EventClientConnection;
import me.semx11.autotip.event.EventClientTick;
import me.semx11.autotip.gson.AnnotationExclusionStrategy;
import me.semx11.autotip.util.Config;
import me.semx11.autotip.util.ErrorReport;
import me.semx11.autotip.util.FileUtil;
import me.semx11.autotip.util.LegacyFileUtil;
import me.semx11.autotip.util.MessageUtil;
import me.semx11.autotip.util.MinecraftVersion;
import me.semx11.autotip.util.NioWrapper;
import me.semx11.autotip.util.UniversalUtil;
import me.semx11.autotip.util.Version;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Autotip.MOD_ID, version = Autotip.VERSION, clientSideOnly = true, acceptedMinecraftVersions = "[1.8, 1.12.2]")
public class Autotip {

    public static final Logger LOGGER = LogManager.getLogger("Autotip");

    static final String MOD_ID = "autotip";
    static final String VERSION = "2.1.0.7";

    @Instance
    private static Autotip instance;

    private Minecraft minecraft;
    private MinecraftVersion mcVersion;
    private Version version;

    private Gson gson;
    private String userDirString;

    private FileUtil fileUtil;
    private MessageUtil messageUtil;

    private Config config;
    private TaskManager taskManager;
    private SessionManager sessionManager;

    public static Autotip getInstance() {
        return instance;
    }

    public Minecraft getMinecraft() {
        return minecraft;
    }

    public GameProfile getGameProfile() {
        return getMinecraft().getSession().getProfile();
    }

    public MinecraftVersion getMcVersion() {
        return mcVersion;
    }

    public Version getVersion() {
        return version;
    }

    public Gson getGson() {
        return gson;
    }

    public String getUserDirString() {
        return userDirString;
    }

    public FileUtil getFileUtil() {
        return fileUtil;
    }

    public MessageUtil getMessageUtil() {
        return messageUtil;
    }

    public Config getConfig() {
        return config;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        this.minecraft = Minecraft.getMinecraft();
        this.mcVersion = UniversalUtil.getMinecraftVersion();
        this.version = new Version(VERSION);

        this.gson = new GsonBuilder()
                .setExclusionStrategies(new AnnotationExclusionStrategy())
                .setPrettyPrinting()
                .create();

        // TODO: Remove because it's legacy
        this.userDirString = NioWrapper.separator("mods/autotip/" + getGameProfile().getId() + "/");

        this.messageUtil = new MessageUtil();

        try {
            this.fileUtil = new FileUtil(this);
            this.fileUtil.createDirectories();

            this.config = new Config(this).load();
            this.taskManager = new TaskManager();
            this.sessionManager = new SessionManager(this);

            this.registerEvents(
                    EventClientTick.getInstance(),
                    EventClientConnection.getInstance(),
                    EventChatReceived.getInstance()
            );
            this.registerCommands(
                    CommandAutotip.getInstance(),
                    CommandTipHistory.getInstance(),
                    CommandLimbo.getInstance()
            );
            LegacyFileUtil.getVars();
        } catch (IOException e) {
            messageUtil.send("Autotip is disabled because it couldn't create the required files.");
            this.registerEvents(EventClientTick.getInstance());
            ErrorReport.reportException(e);
        }
        Runtime.getRuntime().addShutdownHook(new Thread(sessionManager::logout));
    }

    private void registerEvents(Object... events) {
        Arrays.asList(events).forEach((event) -> {
            MinecraftForge.EVENT_BUS.register(event);
            FMLCommonHandler.instance().bus().register(event);
        });
    }

    private void registerCommands(CommandAbstract... commands) {
        Arrays.asList(commands).forEach(ClientCommandHandler.instance::registerCommand);
    }

}