package eu.donyka.smoothperspective.client;

import eu.donyka.smoothperspective.client.animation.PerspectiveTransitionController;
import eu.donyka.smoothperspective.client.config.SmoothPerspectiveConfigManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public final class SmoothPerspectiveClient implements ClientModInitializer {
    public static final PerspectiveTransitionController TRANSITIONS = new PerspectiveTransitionController();

    @Override
    public void onInitializeClient() {
        SmoothPerspectiveConfigManager.load();
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> TRANSITIONS.sync(client.options.getCameraType()));
    }

    public static Screen createConfigScreen(Screen parent) {
        return eu.donyka.smoothperspective.client.config.SmoothPerspectiveConfigScreen.create(parent);
    }

    public static boolean isCameraClipEnabled() {
        return SmoothPerspectiveConfigManager.get().cameraClip;
    }

    public static float getConfiguredDistance() {
        return (float) SmoothPerspectiveConfigManager.get().distance;
    }

    public static Minecraft getClient() {
        return Minecraft.getInstance();
    }
}
