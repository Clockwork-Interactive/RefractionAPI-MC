package net.refractionapi.refraction.networking.S2C;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.feature.cutscenes.client.ClientCutsceneData;
import net.refractionapi.refraction.networking.Packet;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class InvokeCutsceneS2CPacket extends Packet {
    private final int cameraID;
    private final boolean start;

    public InvokeCutsceneS2CPacket(int id, boolean start) {
        this.cameraID = id;
        this.start = start;
    }

    public InvokeCutsceneS2CPacket(FriendlyByteBuf buf) {
        cameraID = buf.readInt();
        start = buf.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(cameraID);
        buf.writeBoolean(start);
    }

    @Override
    public void handle(@Nullable Player player, Consumer<Runnable> context) {
        context.accept(() -> ClientCutsceneData.startCutscene(this.cameraID, this.start));
    }

}
