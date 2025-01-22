package net.refractionapi.refraction.helper.math;

/**
 * Thanks to <a href="https://bsky.app/profile/backupcup.dev">BackupCup</a> for this :P <br>
 * (taken from Hexed)
 */
public class ColorUtils {
    public static int getCurrentColorRendering(int colorStart, int colorEnd, long currentTick) {
        float position = (currentTick % 100f) / 100f;

        if (position <= 0.5f) {
            return interpolateColor(colorStart, colorEnd, position * 2);
        } else {
            return interpolateColor(colorEnd, colorStart, (position - 0.5f) * 2);
        }
    }

    public static int interpolateColor(int color1, int color2, float ratio) {
        int r = interpolateComponent((color1 >> 16) & 0xFF, (color2 >> 16) & 0xFF, ratio);
        int g = interpolateComponent((color1 >> 8) & 0xFF, (color2 >> 8) & 0xFF, ratio);
        int b = interpolateComponent(color1 & 0xFF, color2 & 0xFF, ratio);

        return (r << 16) | (g << 8) | b;
    }

    private static int interpolateComponent(int c1, int c2, float ratio) {
        return Math.round(c1 + (c2 - c1) * ratio);
    }
}
