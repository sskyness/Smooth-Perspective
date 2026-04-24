package eu.donyka.smoothperspective.mixin.client;

import eu.donyka.smoothperspective.client.SmoothPerspectiveClient;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow
    protected abstract void move(double distanceOffset, double verticalOffset, double horizontalOffset);

    @Shadow
    protected abstract void setRotation(float yaw, float pitch);

    @Shadow
    protected abstract double getMaxZoom(double desiredCameraDistance);

    @ModifyVariable(method = "setup", at = @At("HEAD"), argsOnly = true, index = 3)
    private boolean smoothPerspective$forceDetached(boolean detached) {
        return detached || SmoothPerspectiveClient.TRANSITIONS.shouldForceDetached();
    }

    @Redirect(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;getMaxZoom(D)D"))
    private double smoothPerspective$useConfiguredDistance(Camera instance, double desiredCameraDistance) {
        double configuredDistance = SmoothPerspectiveClient.getConfiguredDistance();
        double renderedDistance = SmoothPerspectiveClient.TRANSITIONS.getRenderedDistance((float) configuredDistance);
        return SmoothPerspectiveClient.isCameraClipEnabled()
                ? renderedDistance
                : getMaxZoom(renderedDistance);
    }

    @Redirect(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;move(DDD)V"))
    private void smoothPerspective$applyAnimatedPose(
            Camera instance,
            double distanceOffset,
            double verticalOffset,
            double horizontalOffset,
            BlockGetter level,
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
