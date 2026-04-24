package eu.donyka.smoothperspective.client.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.Locale;

public final class SmoothPerspectiveConfigScreen {
    private SmoothPerspectiveConfigScreen() {
    }

    public static Screen create(Screen parent) {
        SmoothPerspectiveConfig editableConfig = SmoothPerspectiveConfigManager.get().copy();
        SmoothPerspectiveConfig defaults = new SmoothPerspectiveConfig();

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(translatable("smooth_perspective.config.title"));
        builder.setSavingRunnable(() -> SmoothPerspectiveConfigManager.update(editableConfig));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory general = builder.getOrCreateCategory(translatable("smooth_perspective.config.category.general"));

        general.addEntry(entryBuilder
                .startIntSlider(
                        translatable("smooth_perspective.config.animation_duration"),
                        editableConfig.animationDurationMs,
                        0,
                        1000
                )
                .setDefaultValue(defaults.animationDurationMs)
                .setTextGetter(value -> translatable("smooth_perspective.config.animation_duration.value", value))
                .setSaveConsumer(value -> editableConfig.animationDurationMs = value)
                .build());

        general.addEntry(entryBuilder
                .startIntSlider(
                        translatable("smooth_perspective.config.distance"),
                        (int) Math.round(editableConfig.distance * 10.0D),
                        10,
                        160
                )
                .setDefaultValue((int) Math.round(defaults.distance * 10.0D))
                .setTextGetter(value -> translatable(
                        "smooth_perspective.config.distance.value",
                        String.format(Locale.US, "%.1f", value / 10.0D)
                ))
                .setSaveConsumer(value -> editableConfig.distance = value / 10.0D)
                .build());

        general.addEntry(entryBuilder
                .startBooleanToggle(
                        translatable("smooth_perspective.config.camera_clip"),
                        editableConfig.cameraClip
                )
                .setDefaultValue(defaults.cameraClip)
                .setSaveConsumer(value -> editableConfig.cameraClip = value)
                .build());

        return builder.build();
    }

    private static Component translatable(String key, Object... args) {
        return new TranslatableComponent(key, args);
    }
}
