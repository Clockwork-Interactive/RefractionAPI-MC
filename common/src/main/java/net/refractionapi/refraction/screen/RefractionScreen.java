package net.refractionapi.refraction.screen;

import net.minecraft.nbt.CompoundTag;
import net.refractionapi.refraction.client.ClientData;

public interface RefractionScreen {

    void handleServerEvent(CompoundTag tag);

    default void sendData(CompoundTag tag) {
        ClientData.screenHandler.sendData(tag);
    }

}
