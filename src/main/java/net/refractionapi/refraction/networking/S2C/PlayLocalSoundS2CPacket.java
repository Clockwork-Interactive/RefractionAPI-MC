package net.refractionapi.refraction.networking.S2C;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.refractionapi.refraction.networking.Packet;

import java.util.function.Supplier;

public class PlayLocalSoundS2CPacket extends Packet {

    final ResourceLocation sound;

    public PlayLocalSoundS2CPacket(SoundEvent sound) {
        this.sound = sound.getLocation();
    }

    public PlayLocalSoundS2CPacket(FriendlyByteBuf buf) {
        sound = buf.readResourceLocation();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeResourceLocation(sound);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            SoundEvent event = ForgeRegistries.SOUND_EVENTS.getValue(this.sound);
            if (event == null) return;
            Minecraft.getInstance().getSoundManager().play(new EntityBoundSoundInstance(event, SoundSource.AMBIENT, 1.0F, 1.0F, Minecraft.getInstance().player, RandomSource.create().nextLong()));
        });
        context.setPacketHandled(true);
    }
}
