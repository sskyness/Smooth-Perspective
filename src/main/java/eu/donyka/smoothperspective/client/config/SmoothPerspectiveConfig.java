package eu.donyka.smoothperspective.client.config;

public final class SmoothPerspectiveConfig {
    public int animationDurationMs = 250;
    public double distance = 4.0D;
    public boolean cameraClip = false;

    public SmoothPerspectiveConfig copy() {
        SmoothPerspectiveConfig copy = new SmoothPerspectiveConfig();
        copy.animationDurationMs = animationDurationMs;
        copy.distance = distance;
        copy.cameraClip = cameraClip;
        return copy;
    }

    public void sanitize() {
        animationDurationMs = clamp(animationDurationMs, 0, 1000);
        distance = clamp(distance, 1.0D, 16.0D);
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
