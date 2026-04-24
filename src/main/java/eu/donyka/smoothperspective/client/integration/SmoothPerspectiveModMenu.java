package eu.donyka.smoothperspective.client.integration;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import eu.donyka.smoothperspective.client.SmoothPerspectiveClient;

public final class SmoothPerspectiveModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return SmoothPerspectiveClient::createConfigScreen;
    }
}
