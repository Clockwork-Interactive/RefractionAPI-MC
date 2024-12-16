package net.refractionapi.refraction.networking.S2C;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.mixininterfaces.ICameraMixin;
import net.refractionapi.refraction.networking.Packet;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class InvokeCameraShakeS2CPacket extends Packet {
    private final int durationInTicks;
    private final float intensity;

    public InvokeCameraShakeS2CPacket(int durationInTicks, float intensity) {
        this.durationInTicks = durationInTicks;
        this.intensity = intensity;
    }

    public InvokeCameraShakeS2CPacket(FriendlyByteBuf buf) {
        durationInTicks = buf.readInt();
        intensity = buf.readFloat();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(durationInTicks);
        buf.writeFloat(intensity);
    }

    @Override
    public void handle(@Nullable Player player, Consumer<Runnable> context) {
        context.accept(() -> {
            if (Minecraft.getInstance().gameRenderer.getMainCamera() instanceof ICameraMixin cameraMixin) {
                cameraMixin.startCameraShake(durationInTicks, intensity);
            }
        });
    }

}
