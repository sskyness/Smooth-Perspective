package eu.donyka.smoothperspective.mixin.client;

import eu.donyka.smoothperspective.client.SmoothPerspectiveClient;
import net.minecraft.client.CameraType;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Options.class)
public abstract class OptionsMixin {
    @Shadow private CameraType cameraType;

    @Inject(method = "setCameraType", at = @At("HEAD"))
    private void smoothPerspective$onCameraTypeChanged(CameraType newType, CallbackInfo callbackInfo) {
        SmoothPerspectiveClient.TRANSITIONS.onCameraTypeChanged(cameraType, newType);
    }
}
