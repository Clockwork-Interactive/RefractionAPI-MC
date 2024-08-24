package net.refractionapi.refraction.networking;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.refractionapi.refraction.networking.C2S.SendScreenDataC2SPacket;
import net.refractionapi.refraction.networking.C2S.SyncInteractionC2SPacket;
import net.refractionapi.refraction.networking.S2C.*;
import net.refractionapi.refraction.platform.RefractionServices;

public interface RefractionMessages {

    static void register() {
        registerPacket(InvokeCutsceneS2CPacket.class, RNetworkDirection.PLAY_TO_CLIENT);
        registerPacket(InvokeCameraShakeS2CPacket.class, RNetworkDirection.PLAY_TO_CLIENT);
        registerPacket(EnablePlayerMovementS2CPacket.class, RNetworkDirection.PLAY_TO_CLIENT);
        registerPacket(PlayLocalSoundS2CPacket.class, RNetworkDirection.PLAY_TO_CLIENT);
        registerPacket(SetFOVS2CPacket.class, RNetworkDirection.PLAY_TO_CLIENT);
        registerPacket(SetZRotS2CPacket.class, RNetworkDirection.PLAY_TO_CLIENT);
        registerPacket(SetBarPropsS2CPacket.class, RNetworkDirection.PLAY_TO_CLIENT);
        registerPacket(SyncQuestInfoS2CPacket.class, RNetworkDirection.PLAY_TO_CLIENT);
        registerPacket(TrackingSoundS2CPacket.class, RNetworkDirection.PLAY_TO_CLIENT);
        registerPacket(AttachTickableSoundS2CPacket.class, RNetworkDirection.PLAY_TO_CLIENT);
        registerPacket(HandleInteractionS2CPacket.class, RNetworkDirection.PLAY_TO_CLIENT);
        registerPacket(SyncInteractionC2SPacket.class, RNetworkDirection.PLAY_TO_SERVER);
        registerPacket(SetScreenS2CPacket.class, RNetworkDirection.PLAY_TO_CLIENT);
        registerPacket(SendScreenDataS2CPacket.class, RNetworkDirection.PLAY_TO_CLIENT);
        registerPacket(SendScreenDataC2SPacket.class, RNetworkDirection.PLAY_TO_SERVER);
    }

    static <MSG extends Packet> void sendToServer(MSG message) {
        RefractionServices.MESSAGES.sendServer(message);
    }

    static <MSG extends Packet> void sendToPlayer(MSG message, ServerPlayer player) {
        RefractionServices.MESSAGES.sendPlayer(message, player);
    }

    static <MSG extends Packet> void sendToAllTracking(MSG message, LivingEntity player) {
        RefractionServices.MESSAGES.sendAllTracking(message, player);
    }

    <P extends Packet> void register(Class<P> msgClass, RNetworkDirection direction);

    <MSG extends Packet> void sendServer(MSG message);

    <MSG extends Packet> void sendPlayer(MSG message, ServerPlayer player);

    <MSG extends Packet> void sendAllTracking(MSG message, LivingEntity player);

    static <P extends Packet> void registerPacket(Class<P> msgClass, RNetworkDirection direction) {
        RefractionServices.MESSAGES.register(msgClass, direction);
    }

    enum RNetworkDirection {
        PLAY_TO_CLIENT,
        PLAY_TO_SERVER
    }

}
