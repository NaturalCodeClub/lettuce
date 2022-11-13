package catserver.server;

import catserver.server.launch.LibrariesManager;
import java.io.File;
import java.nio.file.Files;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

public class SparkLoader {
    private static final boolean enableSpark = Boolean.parseBoolean(System.getProperty("catserver.spark.enable", "true"));

    private static boolean isDefaultInstallSpark() {
        return enableSpark && CatServer.getConfig().defaultInstallPluginSpark;
    }

    public static boolean isSparkPluginEnabled() {
        Plugin sparkPlugin = Bukkit.getServer().getPluginManager().getPlugin("spark");
        return sparkPlugin != null && sparkPlugin.isEnabled();
    }

    public static void tryLoadSparkPlugin(SimplePluginManager pluginManager) {
        if (!isDefaultInstallSpark() || pluginManager.getPlugin("spark") != null) return;

        try {
            File sparkPluginOriginFile = new File(LibrariesManager.librariesDir, LibrariesManager.sparkPluginFileName);
            File sparkPluginFile = new File("plugins", sparkPluginOriginFile.getName());

            if (sparkPluginOriginFile.exists() && !sparkPluginFile.exists()) {
                Files.copy(sparkPluginOriginFile.toPath(), sparkPluginFile.toPath());
            }

            if (sparkPluginFile.exists()) {
                Plugin sparkPlugin = pluginManager.loadPlugin(sparkPluginFile);
                sparkPlugin.onLoad();
                pluginManager.enablePlugin(sparkPlugin);
            } else {
                CatServer.log.warn("Missing " + sparkPluginOriginFile.getAbsolutePath());
            }
        } catch (Exception e) {
            new RuntimeException("Failed to load spark!", e).printStackTrace();
        }
    }
}
