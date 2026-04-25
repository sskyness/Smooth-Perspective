package eu.donyka.camera.client.config;

public final class Config {
    public int animationDurationMs = 250;
    public double distance = 4.0D;

    public Config copy() {
        Config copy = new Config();
        copy.animationDurationMs = animationDurationMs;
        copy.distance = distance;
        return copy;
    }

    public void sanitize() {
        animationDurationMs = (int) clamp(animationDurationMs, 0, 1000);
        distance = clamp((float) distance, 1.0F, 16.0F);
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}
