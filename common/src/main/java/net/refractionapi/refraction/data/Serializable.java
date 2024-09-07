package net.refractionapi.refraction.data;

import net.minecraft.network.FriendlyByteBuf;

/**
 * Later implementation for generic data serializers <br>
 * Similar to {@link net.minecraft.network.syncher.EntityDataSerializer}
 */
public interface Serializable {

    void write(FriendlyByteBuf buf);

    void read(FriendlyByteBuf buf);

}
