package net.refractionapi.refraction.networking.S2C;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.refractionapi.refraction.cutscenes.client.ClientCutsceneData;
import net.refractionapi.refraction.networking.Packet;

public class InvokeCutsceneS2CPacket extends Packet {
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

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> ClientCutsceneData.startCutscene(this.cameraID, this.start));
        context.setPacketHandled(true);
    }

}
