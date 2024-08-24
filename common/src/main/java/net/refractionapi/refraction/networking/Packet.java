package net.refractionapi.refraction.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.Refraction;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public abstract class Packet {

    public Packet() {
    }

    public Packet(FriendlyByteBuf buf) {
    }

    public ResourceLocation getId() {
        return new ResourceLocation(Refraction.MOD_ID, this.getClass().getSimpleName().toLowerCase());
    }

    public abstract void write(FriendlyByteBuf buf);

    public abstract void handle(@Nullable Player player, Consumer<Runnable> context);

}
