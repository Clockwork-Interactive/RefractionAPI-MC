package net.refractionapi.refraction.networking;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.networking.S2C.InvokeCameraShakeS2CPacket;
import net.refractionapi.refraction.networking.S2C.InvokeCutsceneS2CPacket;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class ModMessages {
    private static final SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(Refraction.MOD_ID, "messages"))
            .networkProtocolVersion(() -> "1.0")
            .clientAcceptedVersions(s -> true)
            .serverAcceptedVersions(s -> true)
            .simpleChannel();

    private static int packetId = 0;

    private static int id() {
        return packetId++;
    }

    public static void register() {
        registerPacket(InvokeCutsceneS2CPacket.class, NetworkDirection.PLAY_TO_CLIENT);
        registerPacket(InvokeCameraShakeS2CPacket.class, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayerEntity player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    private static <P extends Packet> void registerPacket(Class<P> msgClass, NetworkDirection direction) {
        INSTANCE.messageBuilder(msgClass, id(), direction)
                .decoder(byteBuf -> {
                    try {
                        return msgClass.getConstructor(PacketBuffer.class).newInstance(byteBuf);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .encoder(Packet::toBytes)
                .consumer((BiConsumer<P, Supplier<NetworkEvent.Context>>) (msg, supplier) -> msg.handle(supplier.get()))
                .add();
    }

}
