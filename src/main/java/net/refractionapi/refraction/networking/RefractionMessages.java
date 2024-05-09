package net.refractionapi.refraction.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.*;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.networking.S2C.*;

public class RefractionMessages {
    public static final SimpleChannel INSTANCE = ChannelBuilder.named(new ResourceLocation(Refraction.MOD_ID, "main")).simpleChannel();

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
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.send(message, PacketDistributor.SERVER.noArg());
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(message, PacketDistributor.PLAYER.with(player));
    }

    private static <P extends Packet, B extends FriendlyByteBuf> void registerPacket(Class<P> msgClass, NetworkDirection<B> direction) {
        INSTANCE.messageBuilder(msgClass, id(), direction)
                .decoder(byteBuf -> {
                    try {
                        return msgClass.getConstructor(FriendlyByteBuf.class).newInstance(byteBuf);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .encoder(Packet::toBytes)
                .consumerMainThread(Packet::handle)
                .add();
    }

}
