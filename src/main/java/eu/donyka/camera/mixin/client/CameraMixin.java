package eu.donyka.camera.mixin.client;

import eu.donyka.camera.client.Client;
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
    private boolean camera$forceDetached(boolean detached) {
        return detached || Client.ANIMATOR.shouldForceDetached();
    }

    @Redirect(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;getMaxZoom(D)D"))
    private double camera$useConfiguredDistance(Camera instance, double desiredCameraDistance) {
        double configuredDistance = Client.getConfiguredDistance();
        double renderedDistance = Client.ANIMATOR.getRenderedDistance((float) configuredDistance);
        return getMaxZoom(renderedDistance);
    }

    @Redirect(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;move(DDD)V"))
    private void camera$applyAnimatedPose(
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
        if (!Client.ANIMATOR.isTransitionActive()) {
            move(distanceOffset, verticalOffset, horizontalOffset);
            return;
        }

        setRotation(
                Client.ANIMATOR.getRenderedYaw(entity.getViewYRot(partialTick)),
                Client.ANIMATOR.getRenderedPitch(entity.getViewXRot(partialTick))
        );
        move(distanceOffset, verticalOffset, horizontalOffset);
    }
}
