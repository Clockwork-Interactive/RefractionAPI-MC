package net.refractionapi.refraction.string;

import org.apache.commons.lang3.text.WordUtils;

import java.util.Optional;

public class StringUtils {

    /**
     * Formats registry strings. <br>
     * For example "minecraft:oak_sapling" formats to "Oak Sapling"
     *
     * @param string String to format.
     * @return Formatted string.
     */
    public static String formatRegistryName(String string) {
        return WordUtils.capitalize(string.substring(string.indexOf(":") + 1).replace("_", " "));
    }

    /**
     * Parses given string to return a float <br>
     * Examples: <br>
     * " A123.0ASF" -> 123.0F <br>
     * "41D12.44 5D" -> 4112.445F <br>
     * "STRING :P" -> 0.0F
     */
    public static float floatFromString(String string) {
        return Optional.ofNullable(string).filter((s) -> !s.isEmpty()).map((s) -> Float.parseFloat(s.trim().replaceAll("[^0-9.] ", ""))).orElse(0.0F);
    }

}
