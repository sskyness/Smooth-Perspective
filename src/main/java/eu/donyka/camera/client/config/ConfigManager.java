package eu.donyka.camera.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.donyka.camera.Core;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("smooth-perspective.json");

    private static Config config = new Config();

    private ConfigManager() {
    }

    public static Config get() {
        return config;
    }

    public static void update(Config updatedConfig) {
        updatedConfig.sanitize();
        config = updatedConfig.copy();
        save();
    }

    public static void load() {
        if (!Files.exists(CONFIG_PATH)) {
            config.sanitize();
            save();
            return;
        }

        try {
            String rawJson = Files.readString(CONFIG_PATH);
            Config loaded = GSON.fromJson(rawJson, Config.class);
            config = loaded == null ? new Config() : loaded;
            config.sanitize();
        } catch (Exception exception) {
            Core.LOGGER.error("Failed to load Smooth Perspective config, using defaults.", exception);
            config = new Config();
            config.sanitize();
        }
    }

    public static void save() {
        try {
            config.sanitize();
            Files.createDirectories(CONFIG_PATH.getParent());
            Files.writeString(CONFIG_PATH, GSON.toJson(config));
        } catch (IOException exception) {
            Core.LOGGER.error("Failed to save Smooth Perspective config.", exception);
        }
    }
}
