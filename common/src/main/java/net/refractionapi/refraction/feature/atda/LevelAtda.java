package net.refractionapi.refraction.feature.atda;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

public class LevelAtda<D extends AtdaData<D>> extends Atda<ServerLevel, D> {
    protected LevelAtda(Class<ServerLevel> clazz, ResourceLocation identifier) {
        super(clazz, identifier);
    }
}
