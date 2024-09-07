package net.refractionapi.refraction.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.resources.ResourceLocation;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.client.ClientData;

public class RefractionClientNetworking {

    public static <P extends Packet> void registerPacket(Class<P> msgClass) {
        ClientPlayNetworking.registerGlobalReceiver(new ResourceLocation(Refraction.MOD_ID, msgClass.getSimpleName().toLowerCase()), (client, handler, buf, responseSender) -> RefractionMessagesFabric.createPacket(msgClass, buf, ClientData.getPlayer(), client));
    }

}
