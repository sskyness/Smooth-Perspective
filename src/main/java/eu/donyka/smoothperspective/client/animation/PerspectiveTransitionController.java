package eu.donyka.smoothperspective.client.animation;

import eu.donyka.smoothperspective.client.SmoothPerspectiveClient;
import eu.donyka.smoothperspective.client.config.SmoothPerspectiveConfigManager;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;

public final class PerspectiveTransitionController {
    private CameraType currentType = CameraType.FIRST_PERSON;
    private PerspectivePoseSpec startPose = PerspectivePoseSpec.fromCameraType(CameraType.FIRST_PERSON);
    private PerspectivePoseSpec endPose = startPose;
    private long transitionStartedAt;
    private int transitionDurationMs;

    public void sync(CameraType cameraType) {
        currentType = cameraType;
        startPose = PerspectivePoseSpec.fromCameraType(cameraType);
        endPose = startPose;
        transitionStartedAt = 0L;
        transitionDurationMs = 0;
    }

    public void onCameraTypeChanged(CameraType oldType, CameraType newType) {
        Minecraft client = SmoothPerspectiveClient.getClient();
        currentType = newType;

        if (client.player == null || client.level == null) {
            sync(newType);
            return;
        }

        PerspectivePoseSpec sourcePose = getCurrentPose(oldType);
        PerspectivePoseSpec targetPose = PerspectivePoseSpec.fromCameraType(newType);
        int configuredDuration = SmoothPerspectiveConfigManager.get().animationDurationMs;

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

        if (SmoothPerspectiveConfigManager.get().animationDurationMs <= 0 || linearProgress(System.currentTimeMillis()) >= 1.0F) {
            finishTransition();
            return false;
        }

        return true;
    }

    public float getRenderedDistance(float configuredDistance) {
        PerspectivePoseSpec pose = getCurrentPose(resolveCurrentType());
        return pose.distanceFactor() * configuredDistance;
    }

    public float getRenderedYaw(float baseYaw) {
        PerspectivePoseSpec pose = getCurrentPose(resolveCurrentType());
        return baseYaw + pose.yawOffsetDegrees();
    }

    public float getRenderedPitch(float basePitch) {
        PerspectivePoseSpec pose = getCurrentPose(resolveCurrentType());
        return basePitch * pose.pitchFactor();
    }

    private PerspectivePoseSpec getCurrentPose(CameraType fallbackType) {
        if (!isTransitionActive()) {
            return PerspectivePoseSpec.fromCameraType(fallbackType);
        }

        float easedProgress = AnimationMath.easeInOutSine(linearProgress(System.currentTimeMillis()));
        return startPose.interpolate(endPose, easedProgress);
    }

    private CameraType resolveCurrentType() {
        Minecraft client = SmoothPerspectiveClient.getClient();
        if (client.options != null && !isTransitionActive()) {
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

    private void finishTransition() {
        sync(resolveCurrentType());
    }

    private static boolean isEffectivelySame(PerspectivePoseSpec sourcePose, PerspectivePoseSpec targetPose) {
        return Math.abs(sourcePose.distanceFactor() - targetPose.distanceFactor()) < 0.0001F
                && Math.abs(sourcePose.yawOffsetDegrees() - targetPose.yawOffsetDegrees()) < 0.0001F
                && Math.abs(sourcePose.pitchFactor() - targetPose.pitchFactor()) < 0.0001F
                && sourcePose.detached() == targetPose.detached();
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}
