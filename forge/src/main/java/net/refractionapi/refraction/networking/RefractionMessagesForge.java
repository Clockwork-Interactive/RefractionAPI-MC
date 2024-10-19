package net.refractionapi.refraction.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.client.ClientData;

public class RefractionMessagesForge implements RefractionMessages{

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

    @Override
    public <P extends Packet> void register(Class<P> msgClass, RNetworkDirection direction) {
        INSTANCE.messageBuilder(msgClass, id(), direction == RNetworkDirection.PLAY_TO_CLIENT ? NetworkDirection.PLAY_TO_CLIENT : NetworkDirection.PLAY_TO_SERVER)
                .decoder(byteBuf -> {
                    try {
                        return msgClass.getConstructor(FriendlyByteBuf.class).newInstance(byteBuf);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .encoder(Packet::write)
                .consumerMainThread((msg, supplier) -> msg.handle(supplier.get().getSender() == null ? ClientData.getPlayer() : supplier.get().getSender(), (c) -> supplier.get().enqueueWork(c)))
                .add();
    }

    @Override
    public <MSG extends Packet> void sendServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    @Override
    public <MSG extends Packet> void sendAll(MSG message, Level level) {
        INSTANCE.send(message, PacketDistributor.ALL.noArg());
    }

    @Override
    public <MSG extends Packet> void sendPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    @Override
    public <MSG extends Packet> void sendAllTracking(MSG message, LivingEntity player) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    }

}
