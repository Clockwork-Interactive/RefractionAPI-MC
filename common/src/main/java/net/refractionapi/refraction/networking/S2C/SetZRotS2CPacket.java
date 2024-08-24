package net.refractionapi.refraction.networking.S2C;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.client.ClientData;
import net.refractionapi.refraction.math.EasingFunctions;
import net.refractionapi.refraction.networking.Packet;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class SetZRotS2CPacket extends Packet {

    private final float startZRot;
    private final float endZRot;
    private final int transitionTicksZRot;
    private final EasingFunctions easingFunctionZRot;


    public SetZRotS2CPacket(float startZRot, float endZRot, int transitionTicksZRot, EasingFunctions easingFunctionZRot) {
        this.startZRot = startZRot;
        this.endZRot = endZRot;
        this.transitionTicksZRot = transitionTicksZRot;
        this.easingFunctionZRot = easingFunctionZRot;
    }

    public SetZRotS2CPacket(float zRot) {
        this.startZRot = zRot;
        this.endZRot = zRot;
        this.transitionTicksZRot = 1;
        this.easingFunctionZRot = EasingFunctions.LINEAR;
    }

    public SetZRotS2CPacket(FriendlyByteBuf buf) {
        this.startZRot = buf.readFloat();
        this.endZRot = buf.readFloat();
        this.transitionTicksZRot = buf.readInt();
        this.easingFunctionZRot = EasingFunctions.values()[buf.readInt()];
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeFloat(this.startZRot);
        buf.writeFloat(this.endZRot);
        buf.writeInt(this.transitionTicksZRot);
        buf.writeInt(this.easingFunctionZRot.ordinal());
    }

    @Override
    public void handle(@Nullable Player player, Consumer<Runnable> context) {
        context.accept(() -> {
            ClientData.startZRot = this.startZRot;
            ClientData.currentZRot = this.startZRot;
            ClientData.endZRot = this.endZRot;
            ClientData.transitionTicksZRot = this.transitionTicksZRot;
            ClientData.progressTrackerZRot = 0;
            ClientData.easingFunctionZRot = this.easingFunctionZRot;
        });
    }

}
