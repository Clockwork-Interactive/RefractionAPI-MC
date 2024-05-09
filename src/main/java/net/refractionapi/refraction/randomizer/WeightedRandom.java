package net.refractionapi.refraction.randomizer;

import java.util.HashMap;

public class WeightedRandom<T> {

    public final HashMap<T, Float> weightedRandomList = new HashMap<>();

    public WeightedRandom() {

    }

    public WeightedRandom<T> add(T t, float weight) {
        this.weightedRandomList.put(t, weight);
        return this;
    }

    public T get() {
        float totalWeight = 0;
        for (float weight : this.weightedRandomList.values()) {
            totalWeight += weight;
        }
        float random = (float) Math.random() * totalWeight;
        for (T t : this.weightedRandomList.keySet()) {
            random -= this.weightedRandomList.get(t);
            if (random <= 0) {
                return t;
            }
        }
        return null;
    }

    public HashMap<T, Float> getPercentages() {
        float totalWeight = 0;
        for (float weight : this.weightedRandomList.values()) {
            totalWeight += weight;
        }
        HashMap<T, Float> percentages = new HashMap<>();
        for (T t : this.weightedRandomList.keySet()) {
            percentages.put(t, this.weightedRandomList.get(t) / totalWeight);
        }
        return percentages;
    }

    @SafeVarargs
    public static <T> WeightedRandom<T> of(T... items) {
        WeightedRandom<T> weightedRandom = new WeightedRandom<>();
        for (T t1 : items) {
            weightedRandom.add(t1, 1.0F);
        }
        return weightedRandom;
    }

    public HashMap<T, Float> getWeightedRandomList() {
        return this.weightedRandomList;
    }

}
