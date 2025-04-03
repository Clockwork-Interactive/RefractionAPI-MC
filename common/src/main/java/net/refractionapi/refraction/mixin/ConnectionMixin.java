package net.refractionapi.refraction.mixin;

import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.Packet;
import net.refractionapi.refraction.gui.RIMGuiInternal;
import net.refractionapi.refraction.gui.tools.RIMNetworkActivity;
import net.refractionapi.refraction.platform.RefractionServices;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public class ConnectionMixin {
    @Inject(at = @At("TAIL"), method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/protocol/Packet;)V")
    private void onChannelRead0(ChannelHandlerContext pContext, Packet<?> pPacket, CallbackInfo ci) {
        if (!RefractionServices.PLATFORM.isClient()) return;
        RIMNetworkActivity networkActivity = RIMGuiInternal.get().byNameAndGroup("Network Activity", "Networking");
        if (networkActivity != null) {
            networkActivity.receivePacket(pPacket.type());
        }
    }

    @Inject(at = @At("TAIL"), method = "sendPacket")
    public void send(Packet<?> pPacket, PacketSendListener pSendListener, boolean pFlush, CallbackInfo ci) {
        if (!RefractionServices.PLATFORM.isClient()) return;
        RIMNetworkActivity networkActivity = RIMGuiInternal.get().byNameAndGroup("Network Activity", "Networking");
        if (networkActivity != null) {
            networkActivity.sendPacket(pPacket.type());
        }
    }
}
