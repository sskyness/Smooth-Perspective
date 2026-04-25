package eu.donyka.camera.client;

import eu.donyka.camera.client.animation.CameraAnimator;
import eu.donyka.camera.client.config.ConfigManager;
import eu.donyka.camera.client.config.ConfigScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public final class Client implements ClientModInitializer {
    public static final CameraAnimator ANIMATOR = new CameraAnimator();

    @Override
    public void onInitializeClient() {
        ConfigManager.load();
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> ANIMATOR.sync(client.options.getCameraType()));
    }

    public static Screen createConfigScreen(Screen parent) {
        return ConfigScreen.create(parent);
    }

    public static float getConfiguredDistance() {
        return (float) ConfigManager.get().distance;
    }

    public static Minecraft getClient() {
        return Minecraft.getInstance();
    }
}
