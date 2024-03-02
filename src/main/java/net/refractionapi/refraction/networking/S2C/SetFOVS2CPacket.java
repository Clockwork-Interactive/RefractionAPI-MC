package net.refractionapi.refraction.networking.S2C;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.refractionapi.refraction.client.ClientData;
import net.refractionapi.refraction.math.EasingFunctions;
import net.refractionapi.refraction.networking.Packet;

public class SetFOVS2CPacket extends Packet {

    private final int startingFOV;
    private final int endFOV;
    private final int transitionTicks;
    private final EasingFunctions easingFunction;

    public SetFOVS2CPacket(int startingFOV, int endFOV, int transitionTicks, EasingFunctions easingFunction) {
        this.startingFOV = startingFOV;
        this.endFOV = endFOV;
        this.transitionTicks = transitionTicks;
        this.easingFunction = easingFunction;
    }

    public SetFOVS2CPacket(int fov) {
        this.startingFOV = fov;
        this.endFOV = fov;
        this.transitionTicks = 1;
        this.easingFunction = EasingFunctions.LINEAR;
    }

    public SetFOVS2CPacket(FriendlyByteBuf buf) {
        this.startingFOV = buf.readInt();
        this.endFOV = buf.readInt();
        this.transitionTicks = buf.readInt();
        this.easingFunction = EasingFunctions.values()[buf.readInt()];
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.startingFOV);
        buf.writeInt(this.endFOV);
        buf.writeInt(this.transitionTicks);
        buf.writeInt(this.easingFunction.ordinal());
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ClientData.startFOV = this.startingFOV;
            ClientData.currentFOV = this.startingFOV;
            ClientData.endFOV = this.endFOV;
            ClientData.transitionTicksFOV = this.transitionTicks;
            ClientData.progressTrackerFOV = 0;
            ClientData.easingFunctionFOV = this.easingFunction;
        });
        context.setPacketHandled(true);
    }
}
