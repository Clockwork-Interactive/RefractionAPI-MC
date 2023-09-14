package net.refractionapi.refraction.networking.S2C;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.refractionapi.refraction.mixininterfaces.ICameraMixin;
import net.refractionapi.refraction.networking.Packet;

public class InvokeCameraShakeS2CPacket extends Packet {
    private int durationInTicks = 0;
    private int intensity = 0;

    public InvokeCameraShakeS2CPacket(int durationInTicks, int intensity) {
        this.durationInTicks = durationInTicks;
        this.intensity = intensity;
    }

    public InvokeCameraShakeS2CPacket(FriendlyByteBuf buf) {
        durationInTicks = buf.readInt();
        intensity = buf.readInt();
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(durationInTicks);
        buf.writeInt(intensity);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            if (Minecraft.getInstance().gameRenderer.getMainCamera() instanceof ICameraMixin) {
                ICameraMixin cameraMixin = (ICameraMixin) Minecraft.getInstance().gameRenderer.getMainCamera();
                cameraMixin.startCameraShake(durationInTicks, intensity);
                context.setPacketHandled(true);
            }
        });
    }
}
