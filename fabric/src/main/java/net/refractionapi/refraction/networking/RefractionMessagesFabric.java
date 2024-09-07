package net.refractionapi.refraction.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.platform.RefractionServices;

public class RefractionMessagesFabric implements RefractionMessages {

    @Override
    public <P extends Packet> void register(Class<P> msgClass, RNetworkDirection direction) {
        if (direction == RNetworkDirection.PLAY_TO_SERVER) {
            ServerPlayNetworking.registerGlobalReceiver(new ResourceLocation(Refraction.MOD_ID, msgClass.getSimpleName().toLowerCase()), (server, player, handler, buf, sender) -> createPacket(msgClass, buf, player, server));
        } else if (RefractionServices.PLATFORM.isClient()){
            RefractionClientNetworking.registerPacket(msgClass); // do I end it all chat --Zeus
        }
    }

    protected static  <P extends Packet> void createPacket(Class<P> msgClass, FriendlyByteBuf buf, Player player, BlockableEventLoop<?> executer) {
        try {
            P packet = msgClass.getConstructor(FriendlyByteBuf.class).newInstance(buf);
            packet.handle(player, executer::execute);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <MSG extends Packet> void sendServer(MSG message) {
        ClientPlayNetworking.send(FabricMessageWrapper.wrap(message));
    }

    @Override
    public <MSG extends Packet> void sendPlayer(MSG message, ServerPlayer player) {
        ServerPlayNetworking.send(player, FabricMessageWrapper.wrap(message));
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
