package eu.donyka.smoothperspective.client.animation;

import net.minecraft.client.CameraType;
import net.minecraft.util.Mth;

public final class PerspectivePoseSpec {
    private final float distanceFactor;
    private final float yawOffsetDegrees;
    private final float pitchFactor;
    private final boolean detached;

    public PerspectivePoseSpec(float distanceFactor, float yawOffsetDegrees, float pitchFactor, boolean detached) {
        this.distanceFactor = distanceFactor;
        this.yawOffsetDegrees = yawOffsetDegrees;
        this.pitchFactor = pitchFactor;
        this.detached = detached;
    }

    public static PerspectivePoseSpec fromCameraType(CameraType cameraType) {
        switch (cameraType) {
            case FIRST_PERSON:
                return new PerspectivePoseSpec(0.0F, 0.0F, 1.0F, false);
            case THIRD_PERSON_BACK:
                return new PerspectivePoseSpec(1.0F, 0.0F, 1.0F, true);
            case THIRD_PERSON_FRONT:
                return new PerspectivePoseSpec(1.0F, 180.0F, -1.0F, true);
            default:
                return new PerspectivePoseSpec(0.0F, 0.0F, 1.0F, false);
        }
    }

    public PerspectivePoseSpec interpolate(PerspectivePoseSpec target, float delta) {
        float clampedDelta = Mth.clamp(delta, 0.0F, 1.0F);
        return new PerspectivePoseSpec(
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
