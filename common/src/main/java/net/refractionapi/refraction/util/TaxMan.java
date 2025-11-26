package net.refractionapi.refraction.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class TaxMan {
    private MutableComponent built;

    public TaxMan(MutableComponent base) {
        this.built = base;
    }

    public TaxMan() {
        this.built = Component.literal("");
    }

    public TaxMan coloredText(String c, int color) {
        built.append(Component.literal(c).withColor(color));
        return this;
    }

    public TaxMan append(String c) {
        built.append(Component.literal(c));
        return this;
    }

    public TaxMan newLine() {
        built.append("\n");
        return this;
    }

    public TaxMan append(Component c) {
        built.append(c);
        return this;
    }

    public TaxMan formatted(ChatFormatting... format) {
        built.withStyle(format);
        return this;
    }

    /**
     * ("The $f{Honored} One", ChatFormatting.GOLD) ->
     * "The Honored One", with Honored being formatted gold
     * ("$f{The} $f{Honored} $f{One}", ChatFormatting.BOLD, ChatFormatting.GOLD, ChatFormatting.BLUE) ->
     * The - Bold, Honored - GOLD, One - BLUE
     */
    public TaxMan formatted(String c, ChatFormatting... format) {
        String[] split = c.split("\\$f\\{");
        for (int i = 0; i < split.length; i++) {
            String part = split[i];
            if (!part.contains("}")) {
                append(part);
                continue;
            }
            String toFormat = part.substring(0, part.indexOf("}"));
            String rest = part.substring(part.indexOf("}") + 1);
            ChatFormatting style = i - 1 < format.length ? format[i - 1] : ChatFormatting.RESET;
            append(Component.literal(toFormat).withStyle(style));
            append(Component.literal(rest));
        }
        return this;
    }

    public TaxMan whiteSpace(int length) {
        return coloredText(" ".repeat(length), 0);
    }

    public Component component() {
        return built;
    }

    public static TaxMan literal(String str) {
        return new TaxMan(Component.literal(str));
    }

    public static TaxMan literal(String str, ChatFormatting... style) {
        return new TaxMan(Component.literal(str).withStyle(style));
    }

    public static TaxMan format(String str, ChatFormatting... format) {
        return new TaxMan().formatted(str, format);
    }
}
