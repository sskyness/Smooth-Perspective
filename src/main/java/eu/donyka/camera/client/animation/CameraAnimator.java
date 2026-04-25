package eu.donyka.camera.client.animation;

import eu.donyka.camera.client.Client;
import eu.donyka.camera.client.config.ConfigManager;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;

public final class CameraAnimator {
    private static final float MIN_DETACHED_DISTANCE = 0.625F;

    private CameraType currentType = CameraType.FIRST_PERSON;
    private CameraPose startPose = CameraPose.fromCameraType(CameraType.FIRST_PERSON);
    private CameraPose endPose = startPose;
    private long transitionStartedAt;
    private int transitionDurationMs;

    public void sync(CameraType cameraType) {
        currentType = cameraType;
        startPose = CameraPose.fromCameraType(cameraType);
        endPose = startPose;
        transitionStartedAt = 0L;
        transitionDurationMs = 0;
    }

    public void onCameraTypeChanged(CameraType oldType, CameraType newType) {
        Minecraft client = Client.getClient();
        currentType = newType;

        if (client.player == null || client.level == null) {
            sync(newType);
            return;
        }

        CameraPose sourcePose = getCurrentPose(oldType);
        CameraPose targetPose = CameraPose.fromCameraType(newType);
        int configuredDuration = ConfigManager.get().animationDurationMs;

        if (configuredDuration <= 0 || isEffectivelySame(sourcePose, targetPose)) {
            sync(newType);
            return;
        }

        startPose = sourcePose;
        endPose = targetPose;
        transitionStartedAt = System.currentTimeMillis();
        transitionDurationMs = configuredDuration;
    }

    public boolean shouldForceDetached() {
        return isTransitionActive() && startPose.detached();
    }

    public boolean isTransitionActive() {
        if (transitionStartedAt == 0L) {
            return false;
        }

        if (ConfigManager.get().animationDurationMs <= 0 || linearProgress(System.currentTimeMillis()) >= 1.0F) {
            finishTransition();
            return false;
        }

        return true;
    }

    public float getRenderedDistance(float configuredDistance) {
        CameraPose pose = getCurrentPose(resolveCurrentType());
        float renderedDistance = pose.distanceFactor() * configuredDistance;
        if (pose.detached()) {
            return Math.max(renderedDistance, MIN_DETACHED_DISTANCE);
        }
        return renderedDistance;
    }

    public float getRenderedYaw(float baseYaw) {
        CameraPose pose = getCurrentPose(resolveCurrentType());
        return baseYaw + pose.yawOffsetDegrees();
    }

    public float getRenderedPitch(float basePitch) {
        CameraPose pose = getCurrentPose(resolveCurrentType());
        return basePitch * pose.pitchFactor();
    }

    private CameraPose getCurrentPose(CameraType fallbackType) {
        if (!isTransitionActive()) {
            return CameraPose.fromCameraType(fallbackType);
        }

        float easedProgress = AnimationMath.easeInOutSine(linearProgress(System.currentTimeMillis()));
        return startPose.interpolate(endPose, easedProgress);
    }

    private CameraType resolveCurrentType() {
        Minecraft client = Client.getClient();
        if (client.options != null && transitionStartedAt == 0L) {
            currentType = client.options.getCameraType();
        }
        return currentType;
    }

    private float linearProgress(long currentTimeMillis) {
        if (transitionDurationMs <= 0) {
            return 1.0F;
        }

        return clamp((currentTimeMillis - transitionStartedAt) / (float) transitionDurationMs, 0.0F, 1.0F);
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    private void finishTransition() {
        Minecraft client = Client.getClient();
        if (client.options != null) {
            currentType = client.options.getCameraType();
        }

        startPose = CameraPose.fromCameraType(currentType);
        endPose = startPose;
        transitionStartedAt = 0L;
        transitionDurationMs = 0;
    }

    private static boolean isEffectivelySame(CameraPose sourcePose, CameraPose targetPose) {
        return Math.abs(sourcePose.distanceFactor() - targetPose.distanceFactor()) < 0.0001F
                && Math.abs(sourcePose.yawOffsetDegrees() - targetPose.yawOffsetDegrees()) < 0.0001F
                && Math.abs(sourcePose.pitchFactor() - targetPose.pitchFactor()) < 0.0001F
                && sourcePose.detached() == targetPose.detached();
    }
}
