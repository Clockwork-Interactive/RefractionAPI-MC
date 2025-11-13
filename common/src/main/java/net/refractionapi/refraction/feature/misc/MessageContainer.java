package net.refractionapi.refraction.feature.misc;

import net.minecraft.world.damagesource.DamageType;
import net.refractionapi.refraction.helper.randomizer.WeightedRandom;
import net.refractionapi.refraction.util.Pair;

import java.util.HashMap;

public class MessageContainer {
    private final HashMap<DamageType, WeightedRandom<Pair<String, String>>> messages = new HashMap<>();
    private final WeightedRandom<Pair<String, String>> all = new WeightedRandom<>();

    public void addEntry(DamageType type, String translatableKey, String msg, float weight) {
        messages.computeIfAbsent(type, k -> new WeightedRandom<>()).add(Pair.of(translatableKey, msg), weight);
    }

    public void addEntry(DamageType type, String key, String string) {
        addEntry(type, key, string, 1.0F);
    }

    public void addEntry(String key, String string, float weight) {
        all.add(Pair.of(key, string), weight);
    }

    public void addEntry(String key, String string) {
        addEntry(key, string, 1.0F);
    }

    @SafeVarargs
    public final void of(Pair<String, String>... strings) {
        for (Pair<String, String> string : strings) addEntry(string.first, string.second);
    }

    public void of(String... keys) {
        for (String key : keys) addEntry(key, "WARNING: not assigned");
    }

    public WeightedRandom<Pair<String, String>> getMessages(DamageType type) {
        return messages.getOrDefault(type, all);
    }
}
