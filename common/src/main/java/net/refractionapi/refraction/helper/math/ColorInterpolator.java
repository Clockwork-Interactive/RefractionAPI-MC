package net.refractionapi.refraction.helper.math;

import java.util.HashMap;
import java.util.stream.Collectors;

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
                .collect(Collectors.toMap(HashMap.Entry::getKey, HashMap.Entry::getValue, (a, b) -> a, HashMap::new));
        return this;
    }

    public ColorInterpolator addPoint(int color, float ratio, float atPosition) {
        return this.addPoint(this.previousColor, color, ratio, atPosition);
    }

    public int getColor(float delta) {
        if (delta < 0 || delta > 1)
            throw new IllegalArgumentException("Can't have delta be smaller than 0 or bigger than 1!");
        for (Float key : this.sections.keySet()) {
            if (delta > key) {
                Section section = this.sections.get(key);
                return interpolateColor(section.fromColor, section.toColor, section.ratio);
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
