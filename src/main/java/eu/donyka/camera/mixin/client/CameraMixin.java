package eu.donyka.camera.mixin.client;

import eu.donyka.camera.client.Client;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow
    protected abstract void move(float distanceOffset, float verticalOffset, float horizontalOffset);

    @Shadow
    protected abstract void setRotation(float yaw, float pitch);

    @Invoker("getMaxZoom")
    protected abstract float camera$invokeGetMaxZoom(float desiredCameraDistance);

    @ModifyVariable(method = "setup", at = @At("HEAD"), argsOnly = true, index = 3)
    private boolean camera$forceDetached(boolean detached) {
        return detached || Client.ANIMATOR.shouldForceDetached();
    }

    @Redirect(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;getMaxZoom(F)F"))
    private float camera$useConfiguredDistance(Camera instance, float desiredCameraDistance) {
        float configuredDistance = Client.getConfiguredDistance();
        float renderedDistance = Client.ANIMATOR.getRenderedDistance(configuredDistance);
        return camera$invokeGetMaxZoom(renderedDistance);
    }

    @Redirect(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;move(FFF)V"))
    private void camera$applyAnimatedPose(
            Camera instance,
            float distanceOffset,
            float verticalOffset,
            float horizontalOffset,
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
