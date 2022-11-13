package gg.m2ke4u.skylight.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;

public class WorkerConfig {
    private static final Logger logger = LogManager.getLogger();
    private static final File CONFIG_FILE = new File(GlobalConfig.CONFIG_DIR,"workers.yml");
    public static final YamlConfiguration CONFIGURATION = new YamlConfiguration();

    public static int ENTITIES_WORKER_THREADS;
    public static int GLOBAL_WORKER_THREADS;
    public static boolean AUTO_CLEAR_WORKERS;
    public static boolean SYNC_ENTITIES;
    public static boolean ASYNC_CATCHER_DISABLED;

    public static void init(){
        try{
            if (!CONFIG_FILE.exists()){
                CONFIG_FILE.createNewFile();
                CONFIGURATION.options().copyDefaults(true);

                ConfigurationSection workerConfig = CONFIGURATION.createSection("workers");
                ConfigurationSection miscConfig = CONFIGURATION.createSection("misc");

                workerConfig.addDefault("entities-worker-threads",Runtime.getRuntime().availableProcessors());
                workerConfig.addDefault("global-worker-threads",Runtime.getRuntime().availableProcessors());
                miscConfig.addDefault("auto-clear-workers",true);
                miscConfig.addDefault("sync-entities-ticking",false);
                miscConfig.addDefault("disable-async-catcher",false);

                ENTITIES_WORKER_THREADS = workerConfig.getInt("entities-worker-threads");
                GLOBAL_WORKER_THREADS = workerConfig.getInt("global-worker-threads");
                AUTO_CLEAR_WORKERS = miscConfig.getBoolean("auto-clear-workers");
                SYNC_ENTITIES = miscConfig.getBoolean("sync-entities-ticking");
                ASYNC_CATCHER_DISABLED = miscConfig.getBoolean("disable-async-catcher");

                CONFIGURATION.load(CONFIG_FILE);
                CONFIGURATION.save(CONFIG_FILE);
                logger.info("[skylight]Worker config inited!");
                return;
            }
            CONFIGURATION.load(CONFIG_FILE);
            ConfigurationSection workerConfig = CONFIGURATION.getConfigurationSection("workers");
            ConfigurationSection miscConfig = CONFIGURATION.getConfigurationSection("misc");

            ENTITIES_WORKER_THREADS = workerConfig.getInt("entities-worker-threads");
            GLOBAL_WORKER_THREADS = workerConfig.getInt("global-worker-threads");
            AUTO_CLEAR_WORKERS = miscConfig.getBoolean("auto-clear-workers");
            SYNC_ENTITIES = miscConfig.getBoolean("sync-entities-ticking");
            ASYNC_CATCHER_DISABLED = miscConfig.getBoolean("disable-async-catcher");
            logger.info("[skylight]Worker config loaded");
        }catch (Exception e){
            logger.error("Failed to create or load config file!",e);
        }
        GlobalConfig.init();
    }
}
