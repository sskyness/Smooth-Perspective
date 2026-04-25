package eu.donyka.camera.mixin.client;

import eu.donyka.camera.client.Client;
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
    private void camera$onCameraTypeChanged(CameraType newType, CallbackInfo callbackInfo) {
        Client.ANIMATOR.onCameraTypeChanged(cameraType, newType);
    }
}
