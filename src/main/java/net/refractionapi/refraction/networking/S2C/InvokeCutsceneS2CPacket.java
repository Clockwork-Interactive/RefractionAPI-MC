package net.refractionapi.refraction.networking.S2C;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.refractionapi.refraction.cutscenes.client.ClientCutsceneData;

import java.util.function.Supplier;

public class InvokeCutsceneS2CPacket {
    private final int cameraID;
    private final boolean start;

    public InvokeCutsceneS2CPacket(int id, boolean start) {
        this.cameraID = id;
        this.start = start;
    }

    public InvokeCutsceneS2CPacket(PacketBuffer buf) {
        cameraID = buf.readInt();
        start = buf.readBoolean();
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeInt(cameraID);
        buf.writeBoolean(start);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> ClientCutsceneData.startCutscene(this.cameraID, this.start));

    }

}
