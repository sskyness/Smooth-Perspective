package eu.donyka.camera.client.animation;

import net.minecraft.client.CameraType;
import net.minecraft.util.Mth;

public final class CameraPose {
    private final float distanceFactor;
    private final float yawOffsetDegrees;
    private final float pitchFactor;
    private final boolean detached;

    public CameraPose(float distanceFactor, float yawOffsetDegrees, float pitchFactor, boolean detached) {
        this.distanceFactor = distanceFactor;
        this.yawOffsetDegrees = yawOffsetDegrees;
        this.pitchFactor = pitchFactor;
        this.detached = detached;
    }

    public static CameraPose fromCameraType(CameraType cameraType) {
        switch (cameraType) {
            case FIRST_PERSON:
                return new CameraPose(0.0F, 0.0F, 1.0F, false);
            case THIRD_PERSON_BACK:
                return new CameraPose(1.0F, 0.0F, 1.0F, true);
            case THIRD_PERSON_FRONT:
                return new CameraPose(1.0F, 180.0F, -1.0F, true);
            default:
                return new CameraPose(0.0F, 0.0F, 1.0F, false);
        }
    }

    public CameraPose interpolate(CameraPose target, float delta) {
        float clampedDelta = Mth.clamp(delta, 0.0F, 1.0F);
        return new CameraPose(
                Mth.lerp(clampedDelta, distanceFactor, target.distanceFactor),
                AnimationMath.lerpDegrees(clampedDelta, yawOffsetDegrees, target.yawOffsetDegrees),
                Mth.lerp(clampedDelta, pitchFactor, target.pitchFactor),
                detached || target.detached
        );
    }

    public float distanceFactor() {
        return distanceFactor;
    }

    public float yawOffsetDegrees() {
        return yawOffsetDegrees;
    }

    public float pitchFactor() {
        return pitchFactor;
    }

    public boolean detached() {
        return detached;
    }
}
