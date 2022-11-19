package gg.m2ke4u.skylight.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;

public class GlobalConfig {
    public static final File CONFIG_DIR = new File("lettuce-configs");
    public static final File CONFIG_FILE = new File(CONFIG_DIR,"global.yml");
    private static final Logger logger = LogManager.getLogger();
    public static final YamlConfiguration CONFIGURATION = new YamlConfiguration();

    public static boolean FORGE_EVENT_RETURN_TO_MAIN = false;
    public static boolean BUKKIT_EVENT_RETURN_TO_MAIN = false;

    static{
        if (!CONFIG_DIR.exists()){
            CONFIG_DIR.mkdirs();
            logger.info("Config dir created.");
        }
    }

    public static void init(){
        try{
            if (!CONFIG_FILE.exists()){
                CONFIG_FILE.createNewFile();
                CONFIGURATION.options().copyDefaults(true);
                CONFIGURATION.options().header("Warn:未实现");

                ConfigurationSection MISC_SECTION = CONFIGURATION.createSection("misc");

                MISC_SECTION.addDefault("forge-events-to-main",FORGE_EVENT_RETURN_TO_MAIN);
                MISC_SECTION.addDefault("bukkit-events-to-main",BUKKIT_EVENT_RETURN_TO_MAIN);

                CONFIGURATION.load(CONFIG_FILE);
                CONFIGURATION.save(CONFIG_FILE);
                logger.info("[lettuce]Global config inited!");
                return;
            }
            CONFIGURATION.load(CONFIG_FILE);
            CONFIGURATION.options().copyDefaults(true);

            ConfigurationSection MISC_SECTION = CONFIGURATION.getConfigurationSection("misc");

            FORGE_EVENT_RETURN_TO_MAIN = MISC_SECTION.getBoolean("forge-events-to-main");
            BUKKIT_EVENT_RETURN_TO_MAIN = MISC_SECTION.getBoolean("bukkit-events-to-main");
            logger.info("[lettuce]Global config loaded!");
        }catch (Exception e){
            logger.error(e);
        }
    }
}
