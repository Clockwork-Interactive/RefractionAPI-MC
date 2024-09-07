package net.refractionapi.refraction.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.refractionapi.refraction.platform.RefractionServices;

public class RefractionMessagesFabric implements RefractionMessages {

    @Override
    public <P extends Packet> void register(Class<P> msgClass, RNetworkDirection direction) {
        if (direction == RNetworkDirection.PLAY_TO_SERVER) {
            CustomPacketPayload.Type<P> type = Packet.getPacketType(msgClass);
            StreamCodec<FriendlyByteBuf, P> codec = Packet.getCodec(msgClass);
            PayloadTypeRegistry.playC2S().register(type, codec);
            ServerPlayNetworking.registerGlobalReceiver(type, (packet, context) -> packet.handle(context.player(), context.player().getServer()::execute));
        } else if (RefractionServices.PLATFORM.isClient()) {
            RefractionClientNetworking.registerPacket(msgClass);
        }
    }

    @Override
    public <MSG extends Packet> void sendServer(MSG message) {
        ClientPlayNetworking.send(message);
    }

    @Override
    public <MSG extends Packet> void sendPlayer(MSG message, ServerPlayer player) {
        ServerPlayNetworking.send(player, message);
    }

    @Override
    public <MSG extends Packet> void sendAllTracking(MSG message, LivingEntity entity) {
        if (entity instanceof ServerPlayer player)
            sendPlayer(message, player);

        for (ServerPlayer player : PlayerLookup.tracking(entity)) {
            sendPlayer(message, player);
        }
    }

}
