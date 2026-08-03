package net.refractionapi.refraction.feature.scheme;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.refractionapi.refraction.client.RefractionClient;
import net.refractionapi.refraction.feature.twc.TWC;

import java.util.function.Consumer;

public interface RScreen {
    void handle(ScreenScheme.ScreenMessage screenMessage);

    default void handleMenuInit(ScreenScheme.ScreenMessage screenMessage) {

    }

    default void sendNbt(CompoundTag tag) {
        RefractionClient.screenRegistry.screenChannel.sendMessage("default", TWC.message().buf((buf) -> buf.writeNbt(tag)));
    }

    default void sendNbt(Consumer<CompoundTag> tag) {
        CompoundTag compoundTag = new CompoundTag();
        tag.accept(compoundTag);
        sendNbt(compoundTag);
    }

    default void sendBuf(Consumer<FriendlyByteBuf> friendlyByteBuf) {
        RefractionClient.screenRegistry.screenChannel.sendMessage("default", TWC.message().buf(friendlyByteBuf));
    }
}
