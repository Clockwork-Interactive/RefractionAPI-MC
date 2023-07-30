package net.refractionapi.refraction.networking;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.networking.S2C.InvokeCameraShakeS2CPacket;
import net.refractionapi.refraction.networking.S2C.InvokeCutsceneS2CPacket;

public class ModMessages {
    private static SimpleChannel INSTANCE;

    private static int packetId = 0;

    private static int id() {
        return packetId++;
    }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(Refraction.MOD_ID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        net.messageBuilder(InvokeCutsceneS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(InvokeCutsceneS2CPacket::new)
                .encoder(InvokeCutsceneS2CPacket::toBytes)
                .consumer(InvokeCutsceneS2CPacket::handle)
                .add();

        net.messageBuilder(InvokeCameraShakeS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(InvokeCameraShakeS2CPacket::new)
                .encoder(InvokeCameraShakeS2CPacket::toBytes)
                .consumer(InvokeCameraShakeS2CPacket::handle)
                .add();

    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayerEntity player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}
