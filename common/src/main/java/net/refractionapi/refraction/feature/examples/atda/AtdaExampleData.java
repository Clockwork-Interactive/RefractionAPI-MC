package net.refractionapi.refraction.feature.examples.atda;

import net.minecraft.nbt.CompoundTag;
import net.refractionapi.refraction.feature.atda.AtdaData;

public class AtdaExampleData extends AtdaData<AtdaExampleData> {
    public int exampleData = 0;

    @Override
    public void save(CompoundTag tag) {
        tag.putInt("exampleData", this.exampleData);
    }

    @Override
    public void load(CompoundTag tag) {
        this.exampleData = tag.getInt("exampleData");
    }

    @Override
    public void copyFrom(AtdaExampleData data) {
        this.exampleData = data.exampleData;
    }
}
