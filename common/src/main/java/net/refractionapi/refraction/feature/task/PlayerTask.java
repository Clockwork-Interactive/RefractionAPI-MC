package net.refractionapi.refraction.feature.task;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.refractionapi.refraction.client.ClientData;

public abstract class PlayerTask extends Task<ServerPlayer> {
    public PlayerTask(ResourceLocation id, TaskHolder<?, ?> holder, ServerPlayer player) {
        super(id, holder, player);
    }

    public PlayerTask(ServerPlayer player, TaskHolder<?, ?> holder, ResourceLocation id, CompoundTag tag) {
        super(player, holder, id, tag);
    }

    public PlayerTask() {
        super();
    }

    public boolean stopOnDeath() {
        return true;
    }

    @Override
    public boolean shouldStop() {
        return stopOnDeath() && player().isDeadOrDying();
    }

    @Override
    public Level level() {
        return accessor == null ? ClientData.getPlayer().level() : accessor.serverLevel();
    }

    public ServerPlayer player() {
        return accessor;
    }
}
