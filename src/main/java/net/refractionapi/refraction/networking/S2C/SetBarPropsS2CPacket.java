package net.refractionapi.refraction.networking.S2C;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.refractionapi.refraction.cutscenes.client.ClientCutsceneData;
import net.refractionapi.refraction.math.EasingFunctions;
import net.refractionapi.refraction.networking.Packet;

public class SetBarPropsS2CPacket extends Packet {

    private final boolean hasBars;
    private final int barHeight;
    private final int endBarHeight;
    private final float startRot;
    private final float endRot;
    private final int transitionTime;
    private final EasingFunctions easingFunction;

    public SetBarPropsS2CPacket(boolean hasBars, int barHeight, int endBarHeight, float startRot, float endRot, int transitionTime, EasingFunctions easingFunction) {
        this.hasBars = hasBars;
        this.barHeight = barHeight;
        this.endBarHeight = endBarHeight;
        this.startRot = startRot;
        this.endRot = endRot;
        this.transitionTime = transitionTime;
        this.easingFunction = easingFunction;
    }

    public SetBarPropsS2CPacket(FriendlyByteBuf buf) {
        this.hasBars = buf.readBoolean();
        this.barHeight = buf.readInt();
        this.endBarHeight = buf.readInt();
        this.startRot = buf.readFloat();
        this.endRot = buf.readFloat();
        this.transitionTime = buf.readInt();
        this.easingFunction = EasingFunctions.values()[buf.readInt()];
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(this.hasBars);
        buf.writeInt(this.barHeight);
        buf.writeInt(this.endBarHeight);
        buf.writeFloat(this.startRot);
        buf.writeFloat(this.endRot);
        buf.writeInt(this.transitionTime);
        buf.writeInt(this.easingFunction.ordinal());
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ClientCutsceneData.setBarProps(this.hasBars, this.barHeight, this.endBarHeight, this.startRot, this.endRot, this.transitionTime, this.easingFunction);
        });
        context.setPacketHandled(true);
    }

}
