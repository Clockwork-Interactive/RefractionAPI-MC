package net.refractionapi.refraction.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.networking.C2S.SyncInteractionC2SPacket;
import net.refractionapi.refraction.networking.S2C.*;

public class RefractionMessages {
    public static final SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder
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
        registerPacket(EnablePlayerMovementS2CPacket.class, NetworkDirection.PLAY_TO_CLIENT);
        registerPacket(PlayLocalSoundS2CPacket.class, NetworkDirection.PLAY_TO_CLIENT);
        registerPacket(SetFOVS2CPacket.class, NetworkDirection.PLAY_TO_CLIENT);
        registerPacket(SetZRotS2CPacket.class, NetworkDirection.PLAY_TO_CLIENT);
        registerPacket(SetBarPropsS2CPacket.class, NetworkDirection.PLAY_TO_CLIENT);
        registerPacket(SyncQuestInfoS2CPacket.class, NetworkDirection.PLAY_TO_CLIENT);
        registerPacket(TrackingSoundS2CPacket.class, NetworkDirection.PLAY_TO_CLIENT);
        registerPacket(AttachTickableSoundS2CPacket.class, NetworkDirection.PLAY_TO_CLIENT);
        registerPacket(HandleInteractionS2CPacket.class, NetworkDirection.PLAY_TO_CLIENT);
        registerPacket(SyncInteractionC2SPacket.class, NetworkDirection.PLAY_TO_SERVER);
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static <MSG> void sendToAllTracking(MSG message, LivingEntity player) {
        INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), message);
    }

    private static <P extends Packet> void registerPacket(Class<P> msgClass, NetworkDirection direction) {
        INSTANCE.messageBuilder(msgClass, id(), direction)
                .decoder(byteBuf -> {
                    try {
                        return msgClass.getConstructor(FriendlyByteBuf.class).newInstance(byteBuf);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .encoder(Packet::toBytes)
                .consumerMainThread((msg, supplier) -> msg.handle(supplier.get()))
                .add();
    }

}
