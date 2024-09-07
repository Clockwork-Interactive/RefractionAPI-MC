package net.refractionapi.refraction.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.client.ClientData;

public class RefractionClientNetworking {

    public static <P extends Packet> void registerPacket(Class<P> msgClass) {
        CustomPacketPayload.Type<P> type = Packet.getPacketType(msgClass);
        StreamCodec<FriendlyByteBuf, P> codec = Packet.getCodec(msgClass);
        PayloadTypeRegistry.playS2C().register(type, codec);
        ClientPlayNetworking.registerGlobalReceiver(type, (packet, context) -> packet.handle(context.player(), context.client()::execute));
    }

}
