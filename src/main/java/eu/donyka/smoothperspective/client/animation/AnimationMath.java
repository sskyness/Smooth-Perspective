package eu.donyka.smoothperspective.client.animation;

import net.minecraft.util.Mth;

public final class AnimationMath {
    private AnimationMath() {
    }

    // Based on Haru's UI_SMOOTH_IN_OUT easing.
    public static float easeInOutSine(float value) {
        float clamped = Mth.clamp(value, 0.0F, 1.0F);
        return (float) (-(Math.cos(Math.PI * clamped) - 1.0D) / 2.0D);
    }

    public static float lerpDegrees(float delta, float start, float end) {
        float difference = Mth.wrapDegrees(end - start);
        if (difference == -180.0F && end > start) {
            difference = 180.0F;
        }
        return start + delta * difference;
    }
}
