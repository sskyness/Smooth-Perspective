package eu.donyka.camera.client.animation;

import net.minecraft.client.CameraType;
import net.minecraft.util.Mth;

public record CameraPose(float distanceFactor, float yawOffsetDegrees, float pitchFactor, boolean detached) {
    public static CameraPose fromCameraType(CameraType cameraType) {
        return switch (cameraType) {
            case FIRST_PERSON -> new CameraPose(0.0F, 0.0F, 1.0F, false);
            case THIRD_PERSON_BACK -> new CameraPose(1.0F, 0.0F, 1.0F, true);
            case THIRD_PERSON_FRONT -> new CameraPose(1.0F, 180.0F, -1.0F, true);
        };
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
}
