package net.refractionapi.refraction.networking.S2C;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.refractionapi.refraction.mixininterfaces.ICameraMixin;

import java.util.function.Supplier;

public class InvokeCameraShakeS2CPacket {
    private int durationInTicks = 0;
    private int intensity = 0;

    public InvokeCameraShakeS2CPacket(int durationInTicks, int intensity) {
        this.durationInTicks = durationInTicks;
        this.intensity = intensity;
    }

    public InvokeCameraShakeS2CPacket(PacketBuffer buf) {
        durationInTicks = buf.readInt();
        intensity = buf.readInt();
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeInt(durationInTicks);
        buf.writeInt(intensity);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            if (Minecraft.getInstance().gameRenderer.getMainCamera() instanceof ICameraMixin) {
                ICameraMixin cameraMixin = (ICameraMixin) Minecraft.getInstance().gameRenderer.getMainCamera();
                cameraMixin.startCameraShake(durationInTicks, intensity);
                context.setPacketHandled(true);
            }
        });
    }
}
