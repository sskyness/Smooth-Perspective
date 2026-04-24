package eu.donyka.smoothperspective.mixin.client;

import eu.donyka.smoothperspective.client.SmoothPerspectiveClient;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Camera.class)
public abstract class CameraMixin {
    protected abstract void move(float distanceOffset, float verticalOffset, float horizontalOffset);

    protected abstract void setRotation(float yaw, float pitch);

    @Invoker("getMaxZoom")
    protected abstract float smoothPerspective$invokeGetMaxZoom(float desiredCameraDistance);

    @ModifyVariable(method = "setup", at = @At("HEAD"), argsOnly = true, index = 3)
    private boolean smoothPerspective$forceDetached(boolean detached) {
        return detached || SmoothPerspectiveClient.TRANSITIONS.shouldForceDetached();
    }

    @Redirect(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;getMaxZoom(F)F"))
    private float smoothPerspective$useConfiguredDistance(Camera instance, float desiredCameraDistance) {
        float configuredDistance = SmoothPerspectiveClient.getConfiguredDistance();
        float renderedDistance = SmoothPerspectiveClient.TRANSITIONS.getRenderedDistance(configuredDistance);
        return SmoothPerspectiveClient.isCameraClipEnabled()
                ? renderedDistance
                : smoothPerspective$invokeGetMaxZoom(renderedDistance);
    }

    @Redirect(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;move(FFF)V"))
    private void smoothPerspective$applyAnimatedPose(
            Camera instance,
            float distanceOffset,
            float verticalOffset,
            float horizontalOffset,
            Level level,
            Entity entity,
            boolean detached,
            boolean mirrored,
            float partialTick
    ) {
        if (!SmoothPerspectiveClient.TRANSITIONS.isTransitionActive()) {
            move(distanceOffset, verticalOffset, horizontalOffset);
            return;
        }

        setRotation(
                SmoothPerspectiveClient.TRANSITIONS.getRenderedYaw(entity.getViewYRot(partialTick)),
                SmoothPerspectiveClient.TRANSITIONS.getRenderedPitch(entity.getViewXRot(partialTick))
        );
        move(distanceOffset, verticalOffset, horizontalOffset);
    }
}
