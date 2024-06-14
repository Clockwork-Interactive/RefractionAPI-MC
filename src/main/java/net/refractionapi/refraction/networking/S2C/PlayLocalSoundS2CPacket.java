package net.refractionapi.refraction.networking.S2C;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.network.NetworkEvent;
import net.refractionapi.refraction.client.ClientData;
import net.refractionapi.refraction.networking.Packet;

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
            ClientData.playLocalSound(sound);
        });
        context.setPacketHandled(true);
    }
}
