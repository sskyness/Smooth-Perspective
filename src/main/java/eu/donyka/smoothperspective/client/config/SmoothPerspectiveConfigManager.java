package eu.donyka.smoothperspective.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.donyka.smoothperspective.SmoothPerspective;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class SmoothPerspectiveConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("smooth-perspective.json");

    private static SmoothPerspectiveConfig config = new SmoothPerspectiveConfig();

    private SmoothPerspectiveConfigManager() {
    }

    public static SmoothPerspectiveConfig get() {
        return config;
    }

    public static void update(SmoothPerspectiveConfig updatedConfig) {
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
            String rawJson = new String(Files.readAllBytes(CONFIG_PATH), StandardCharsets.UTF_8);
            SmoothPerspectiveConfig loaded = GSON.fromJson(rawJson, SmoothPerspectiveConfig.class);
            config = loaded == null ? new SmoothPerspectiveConfig() : loaded;
            config.sanitize();
        } catch (Exception exception) {
            SmoothPerspective.LOGGER.error("Failed to load Smooth Perspective config, using defaults.", exception);
            config = new SmoothPerspectiveConfig();
            config.sanitize();
        }
    }

    public static void save() {
        try {
            config.sanitize();
            Files.createDirectories(CONFIG_PATH.getParent());
            Files.write(CONFIG_PATH, GSON.toJson(config).getBytes(StandardCharsets.UTF_8));
        } catch (IOException exception) {
            SmoothPerspective.LOGGER.error("Failed to save Smooth Perspective config.", exception);
        }
    }
}
