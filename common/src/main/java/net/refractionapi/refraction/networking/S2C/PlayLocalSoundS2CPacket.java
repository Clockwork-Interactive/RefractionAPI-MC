package net.refractionapi.refraction.networking.S2C;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.client.ClientData;
import net.refractionapi.refraction.networking.Packet;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class PlayLocalSoundS2CPacket extends Packet {

    final ResourceLocation sound;

    public PlayLocalSoundS2CPacket(SoundEvent sound) {
        this.sound = sound.getLocation();
    }

    public PlayLocalSoundS2CPacket(FriendlyByteBuf buf) {
        sound = buf.readResourceLocation();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeResourceLocation(sound);
    }

    @Override
    public void handle(@Nullable Player player, Consumer<Runnable> context) {
        context.accept(() -> {
            ClientData.playLocalSound(sound);
        });
    }

}
