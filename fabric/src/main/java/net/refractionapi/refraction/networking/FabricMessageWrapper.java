package net.refractionapi.refraction.networking;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.function.Function;

// Bruh I'ma kill myself --Zeus
public class FabricMessageWrapper {

    private static final HashMap<ResourceLocation, PacketType<?>> packetTypes = new HashMap<>();

    public static FabricPacket wrap(Packet packet) {
        return new FabricPacket() {
            @Override
            public void write(FriendlyByteBuf buf) {
                packet.write(buf);
            }

            @Override
            public PacketType<?> getType() {
                return createOrGet(packet);
            }
        };
    }

    private static <MSG extends Packet> PacketType<?> createOrGet(MSG packet) {
        return packetTypes.computeIfAbsent(packet.getId(), (id) -> PacketType.create(id, (Function<FriendlyByteBuf, ? extends FabricPacket>) (buf) -> {
            try {
                return (FabricPacket) packet.getClass().getConstructor(FriendlyByteBuf.class).newInstance(buf);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }));
    }

}
