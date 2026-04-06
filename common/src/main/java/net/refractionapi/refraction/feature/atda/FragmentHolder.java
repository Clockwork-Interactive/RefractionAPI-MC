package net.refractionapi.refraction.feature.atda;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.List;

public interface FragmentHolder extends DataHolder {
    HashMap<ResourceLocation, IAtdaProvider> fragments();

    default void addFragment(ResourceLocation resourceLocation, IAtdaProvider provider) {
        fragments().put(resourceLocation, provider);
    }

    default IAtdaProvider getFragment(ResourceLocation resourceLocation) {
        return fragments().get(resourceLocation);
    }

    default List<IAtdaProvider> getFragments() {
        return fragments().values().stream().toList();
    }

    String getSyncID();

    Level getLevel();
}
