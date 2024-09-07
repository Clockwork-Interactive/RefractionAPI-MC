package net.refractionapi.refraction.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.client.ClientData;

public class RefractionMessagesForge implements RefractionMessages {

    public static final SimpleChannel INSTANCE = ChannelBuilder.named(Refraction.id("main")).simpleChannel();

    private static int packetId = 0;

    private static int id() {
        return packetId++;
    }

    @Override
    public <P extends Packet> void register(Class<P> msgClass, RNetworkDirection direction) {
        INSTANCE.messageBuilder(msgClass, id(), direction.equals(RNetworkDirection.PLAY_TO_CLIENT) ? NetworkDirection.PLAY_TO_CLIENT : NetworkDirection.PLAY_TO_SERVER)
                .decoder(byteBuf -> {
                    try {
                        return msgClass.getConstructor(FriendlyByteBuf.class).newInstance(byteBuf);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .encoder(Packet::write)
                .consumerMainThread((packet, context) -> packet.handle(context.isClientSide() ? ClientData.getPlayer() : context.getSender(), context::enqueueWork))
                .add();
    }

    @Override
    public <MSG extends Packet> void sendServer(MSG message) {
        INSTANCE.send(message, PacketDistributor.SERVER.noArg());
    }

    @Override
    public <MSG extends Packet> void sendPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(message, PacketDistributor.PLAYER.with(player));
    }

    @Override
    public <MSG extends Packet> void sendAllTracking(MSG message, LivingEntity living) {
        INSTANCE.send(message, PacketDistributor.TRACKING_ENTITY_AND_SELF.with(living));
    }

}
