package net.refractionapi.refraction.helper.math;

import net.minecraft.util.Mth;

import java.util.HashMap;

import static net.refractionapi.refraction.helper.math.ColorUtils.interpolateColor;

// I originally made this for Hexed, but thought this could be great util --Zeus
public class ColorInterpolator {
    private HashMap<Float, Section> sections = new HashMap<>();
    private int previousColor = -1;

    public ColorInterpolator() {

    }

    public ColorInterpolator addPoint(int fromColor, int toColor, float ratio, float atPosition) {
        if (atPosition < 0 || atPosition > 1)
            throw new IllegalArgumentException("Can't have delta be smaller than 0 or bigger than 1!");
        this.previousColor = toColor;
        this.sections.put(atPosition, new Section(fromColor, toColor, ratio));
        this.sections = this.sections.entrySet().stream()
                .sorted((o1, o2) -> Float.compare(o1.getKey(), o2.getKey()))
                .collect(HashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), HashMap::putAll);
        return this;
    }

    public ColorInterpolator addPoint(int color, float ratio, float atPosition) {
        return this.addPoint(this.previousColor, color, ratio, atPosition);
    }

    public int getColor(float delta) {
        delta = Mth.clamp(delta, 0, 1);
        int index = 0;
        for (Float key : this.sections.keySet()) {
            index++;
            if (delta > key || index == this.sections.size()) {
                Section section = this.sections.get(key);
                return interpolateColor(section.fromColor, section.toColor, (delta - key) / section.ratio);
            }
        }
        return -1;
    }

    public int getOrDefault(float delta, int defaultColor) {
        int color = this.getColor(delta);
        return color == -1 ? defaultColor : color;
    }

    private record Section(int fromColor, int toColor, float ratio) {
    }
}
