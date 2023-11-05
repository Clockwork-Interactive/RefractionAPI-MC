package net.refractionapi.refraction.misc;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.networking.ModMessages;
import net.refractionapi.refraction.networking.S2C.EnablePlayerMovementS2CPacket;
import net.refractionapi.refraction.networking.S2C.PlayLocalSoundS2CPacket;
import net.refractionapi.refraction.runnable.RunnableCooldownHandler;

import java.util.List;

public class RefractionMisc {

    public static final RandomSource random = RandomSource.create();

    public static void enableMovement(ServerPlayer player, boolean canMove) {
        ModMessages.sendToPlayer(new EnablePlayerMovementS2CPacket(canMove), player);
    }

    public static void enableMovement(ServerPlayer player, int ticks) {
        enableMovement(player, false);
        RunnableCooldownHandler.addDelayedRunnable(() -> enableMovement(player, true), ticks);
    }

    public static void playLocalSound(Player player, SoundEvent event) {
        if (player instanceof ServerPlayer serverPlayer) {
            ModMessages.sendToPlayer(new PlayLocalSoundS2CPacket(event), serverPlayer);
        } else if (player instanceof LocalPlayer localPlayer){
            Minecraft.getInstance().getSoundManager().play(new EntityBoundSoundInstance(event, SoundSource.AMBIENT, 1.0F, 1.0F, localPlayer, RandomSource.create().nextLong()));
        }
    }

    public static <T> T getRandom(List<T> list) {
        return list.get(random.nextInt(list.size()));
    }

}
