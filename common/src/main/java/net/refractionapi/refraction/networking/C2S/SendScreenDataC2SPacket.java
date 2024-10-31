package net.refractionapi.refraction.networking.C2S;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.data.RefractionData;
import net.refractionapi.refraction.feature.screen.RefractionScreen;
import net.refractionapi.refraction.feature.screen.ScreenBuilder;
import net.refractionapi.refraction.feature.screen.ServerScreen;
import net.refractionapi.refraction.networking.Packet;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;

public class SendScreenDataC2SPacket extends Packet {

    private final RefractionScreen.Code code;
    private final CompoundTag tag;

    public SendScreenDataC2SPacket(RefractionScreen.Code code, CompoundTag tag) {
        this.code = code;
        this.tag = tag;
    }

    public SendScreenDataC2SPacket(RefractionScreen.Code code) {
        this(code, new CompoundTag());
    }

    public SendScreenDataC2SPacket(CompoundTag tag) {
        this(RefractionScreen.Code.DATA, tag);
    }

    public SendScreenDataC2SPacket(FriendlyByteBuf buf) {
        this.code = RefractionScreen.Code.values()[buf.readVarInt()];
        this.tag = buf.readNbt();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(this.code.ordinal());
        buf.writeNbt(this.tag);
    }

    @Override
    public void handle(@Nullable Player player, Consumer<Runnable> context) {
        context.accept(() -> {
            if (player instanceof ServerPlayer) {
                Optional<ServerScreen> screen = RefractionData.get(player).getScreen();
                if (this.code.equals(RefractionScreen.Code.OPEN)) {
                    ScreenBuilder<?> builder = ScreenBuilder.get(this.tag);
                    if (builder == null || !builder.clientAccessible()) return;
                    Object[] args = builder.deserialize(this.tag);
                    screen.ifPresentOrElse((serverScreen -> {
                        if (serverScreen.canClose())
                            builder.setScreen(player, args);
                    }), () -> builder.setScreen(player, args));
                }
                screen.ifPresent((serverScreen -> {
                    switch (this.code) {
                        case CLOSE -> {
                            if (serverScreen.canClose())
                                serverScreen.close();
                        }
                        case DATA -> serverScreen.handle(this.tag);
                        default -> Refraction.LOGGER.warn("Invalid screen handle request : {}", player.getName().getString());
                    }
                }));
            }
        });
    }

}
