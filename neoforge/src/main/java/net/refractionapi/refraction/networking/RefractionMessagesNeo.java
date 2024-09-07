package net.refractionapi.refraction.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.refractionapi.refraction.Refraction;

import java.util.function.Consumer;

public class RefractionMessagesNeo implements RefractionMessages {

    private static PayloadRegistrar registrar = null;

    @Override
    public <P extends Packet> void register(Class<P> msgClass, RNetworkDirection direction) {
        CustomPacketPayload.Type<P> type = Packet.getPacketType(msgClass);
        StreamCodec<FriendlyByteBuf, P> codec = Packet.getCodec(msgClass);
        if (direction.equals(RNetworkDirection.PLAY_TO_CLIENT)) {
            registrar.playToClient(type, codec, (packet, context) -> packet.handle(context.player(), context::enqueueWork));
        } else {
            registrar.playToServer(type, codec, (packet, context) -> packet.handle(context.player(), context::enqueueWork));
        }
    }

    @Override
    public <MSG extends Packet> void sendServer(MSG message) {
        PacketDistributor.sendToServer(message);
    }

    @Override
    public <MSG extends Packet> void sendPlayer(MSG message, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, message);
    }

    @Override
    public <MSG extends Packet> void sendAllTracking(MSG message, LivingEntity entity) {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, message);
    }

    public static void init(IEventBus eventBus) {
        eventBus.addListener((Consumer< RegisterPayloadHandlersEvent>) event -> {
            registrar = event.registrar(Refraction.MOD_ID);
            RefractionMessages.register();
            registrar = null;
        });
    }

}
