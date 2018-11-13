package me.semx11.autotip;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.GameProfile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import me.semx11.autotip.api.RequestHandler;
import me.semx11.autotip.api.reply.impl.LocaleReply;
import me.semx11.autotip.api.reply.impl.SettingsReply;
import me.semx11.autotip.api.request.impl.LocaleRequest;
import me.semx11.autotip.api.request.impl.SettingsRequest;
import me.semx11.autotip.chat.LocaleHolder;
import me.semx11.autotip.chat.MessageUtil;
import me.semx11.autotip.command.CommandAbstract;
import me.semx11.autotip.command.impl.CommandAutotip;
import me.semx11.autotip.command.impl.CommandLimbo;
import me.semx11.autotip.config.Config;
import me.semx11.autotip.config.GlobalSettings;
import me.semx11.autotip.core.MigrationManager;
import me.semx11.autotip.core.SessionManager;
import me.semx11.autotip.core.StatsManager;
import me.semx11.autotip.core.TaskManager;
import me.semx11.autotip.event.Event;
import me.semx11.autotip.event.impl.EventChatReceived;
import me.semx11.autotip.event.impl.EventClientConnection;
import me.semx11.autotip.event.impl.EventClientTick;
import me.semx11.autotip.gson.creator.ConfigCreator;
import me.semx11.autotip.gson.creator.StatsDailyCreator;
import me.semx11.autotip.gson.exclusion.AnnotationExclusionStrategy;
import me.semx11.autotip.stats.StatsDaily;
import me.semx11.autotip.universal.UniversalUtil;
import me.semx11.autotip.util.ErrorReport;
import me.semx11.autotip.util.FileUtil;
import me.semx11.autotip.util.MinecraftVersion;
import me.semx11.autotip.util.Version;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Autotip.MOD_ID, version = Autotip.VERSION, clientSideOnly = true, acceptedMinecraftVersions = "[1.8, 1.12.2]")
public class Autotip {

    public static final Logger LOGGER = LogManager.getLogger("Autotip");

    static final String MOD_ID = "autotip";
    static final String VERSION = "3.0";

    @Instance
    private static Autotip instance;

    private final List<Event> events = new ArrayList<>();
    private final List<CommandAbstract> commands = new ArrayList<>();

    private boolean initialized = false;

    private Minecraft minecraft;
    private MinecraftVersion mcVersion;
    private Version version;

    private Gson gson;

    private FileUtil fileUtil;
    private MessageUtil messageUtil;

    private Config config;
    private GlobalSettings globalSettings;
    private LocaleHolder localeHolder;

    private TaskManager taskManager;
    private SessionManager sessionManager;
    private MigrationManager migrationManager;
    private StatsManager statsManager;

    public static Autotip getInstance() {
        return instance;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public Minecraft getMinecraft() {
        return minecraft;
    }

    public GameProfile getGameProfile() {
        return minecraft.getSession().getProfile();
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

    public FileUtil getFileUtil() {
        return fileUtil;
    }

    public MessageUtil getMessageUtil() {
        return messageUtil;
    }

    public Config getConfig() {
        return config;
    }

    public GlobalSettings getGlobalSettings() {
        return globalSettings;
    }

    public LocaleHolder getLocaleHolder() {
        return localeHolder;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public MigrationManager getMigrationManager() {
        return migrationManager;
    }

    public StatsManager getStatsManager() {
        return statsManager;
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        RequestHandler.setAutotip(this);
        UniversalUtil.setAutotip(this);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        this.minecraft = Minecraft.getMinecraft();
        this.mcVersion = UniversalUtil.getMinecraftVersion();
        this.version = new Version(VERSION);

        this.messageUtil = new MessageUtil(this);
        this.registerEvents(new EventClientTick(this));

        try {
            this.fileUtil = new FileUtil(this);
            this.gson = new GsonBuilder()
                    .registerTypeAdapter(Config.class, new ConfigCreator(this))
                    .registerTypeAdapter(StatsDaily.class, new StatsDailyCreator(this))
                    .setExclusionStrategies(new AnnotationExclusionStrategy())
                    .setPrettyPrinting()
                    .create();

            this.config = new Config(this);
            this.reloadGlobalSettings();
            this.reloadLocale();

            this.taskManager = new TaskManager();
            this.sessionManager = new SessionManager(this);
            this.statsManager = new StatsManager(this);
            this.migrationManager = new MigrationManager(this);

            this.fileUtil.createDirectories();
            this.config.load();
            this.taskManager.getExecutor().execute(() -> {
                this.migrationManager.migrateLegacyFiles();
            });

            this.registerEvents(
                    new EventClientConnection(this),
                    new EventChatReceived(this)
            );
            this.registerCommands(
                    new CommandAutotip(this),
                    new CommandLimbo(this)
            );
            Runtime.getRuntime().addShutdownHook(new Thread(sessionManager::logout));
            this.initialized = true;
        } catch (IOException e) {
            messageUtil.send("Autotip is disabled because it couldn't create the required files.");
            ErrorReport.reportException(e);
        } catch (IllegalStateException e) {
            messageUtil.send("Autotip is disabled because it couldn't connect to the API.");
            ErrorReport.reportException(e);
        }
    }

    public void reloadGlobalSettings() {
        SettingsReply reply = SettingsRequest.of(this).execute();
        if (!reply.isSuccess()) {
            throw new IllegalStateException("Connection error while fetching global settings");
        }
        this.globalSettings = reply.getSettings();
    }

    public void reloadLocale() {
        LocaleReply reply = LocaleRequest.of(this).execute();
        if (!reply.isSuccess()) {
            throw new IllegalStateException("Could not fetch locale");
        }
        this.localeHolder = reply.getLocaleHolder();
    }

    @SuppressWarnings("unchecked")
    public <T extends Event> T getEvent(Class<T> clazz) {
        return (T) events.stream()
                .filter(event -> event.getClass().equals(clazz))
                .findFirst()
                .orElse(null);
    }

    @SuppressWarnings("unchecked")
    public <T extends CommandAbstract> T getCommand(Class<T> clazz) {
        return (T) commands.stream()
                .filter(command -> command.getClass().equals(clazz))
                .findFirst()
                .orElse(null);
    }

    private void registerEvents(Event... events) {
        for (Event event : events) {
            MinecraftForge.EVENT_BUS.register(event);
            FMLCommonHandler.instance().bus().register(event);
            this.events.add(event);
        }
    }

    private void registerCommands(CommandAbstract... commands) {
        for (CommandAbstract command : commands) {
            ClientCommandHandler.instance.registerCommand(command);
            this.commands.add(command);
        }
    }

}