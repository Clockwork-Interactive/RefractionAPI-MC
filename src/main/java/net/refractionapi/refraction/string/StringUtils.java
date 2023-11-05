package net.refractionapi.refraction.string;

import org.apache.commons.lang3.text.WordUtils;

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

}
