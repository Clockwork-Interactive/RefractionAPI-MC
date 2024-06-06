package net.refractionapi.refraction.event;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.cutscenes.Cutscene;
import net.refractionapi.refraction.examples.quest.ExampleQuest;
import net.refractionapi.refraction.math.EasingFunctions;
import net.refractionapi.refraction.misc.RefractionMisc;
import net.refractionapi.refraction.quest.Quest;
import oshi.util.tuples.Pair;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = Refraction.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonForgeEvents {

    public static final HashMap<LivingEntity, Pair<Vec3, Boolean>> frozenEntities = new HashMap<>();
    public static Quest quest;
    public static CompoundTag tag = new CompoundTag();

    @SubscribeEvent
    public static void tickEvent(LivingEvent.LivingTickEvent event) {
        if (event.getEntity().level().isClientSide) return;
        for (Map.Entry<LivingEntity, Pair<Vec3, Boolean>> entry : frozenEntities.entrySet()) {
            LivingEntity entity = entry.getKey();
            Pair<Vec3, Boolean> pair = entry.getValue();
            boolean teleport = pair.getB();
            if (!teleport) continue;
            Vec3 vec3 = pair.getA();
            if (vec3 != null && !(entity instanceof Player)) {
                entity.hurtMarked = true;
                entity.teleportTo(vec3.x, vec3.y, vec3.z);
            }
        }
    }

    @SubscribeEvent
    public static void playerTickEvent(TickEvent.PlayerTickEvent event) {
        if (event.phase.equals(TickEvent.Phase.END)) return;
        if (frozenEntities.containsKey(event.player)) {
            event.player.hurtMarked = true;
            Pair<Vec3, Boolean> pair = frozenEntities.get(event.player);
            boolean teleport = pair.getB();
            if (teleport) {
                Vec3 vec3 = pair.getA();
                event.player.teleportTo(vec3.x, vec3.y, vec3.z);
            }
        }
    }

    @SubscribeEvent
    public static void deathEvent(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player)
            RefractionMisc.enableMovement(player, true);
    }

    @SubscribeEvent
    public static void chat(ServerChatEvent event) {
        if (FMLLoader.isProduction()) return;
        if (event.getMessage().getString().contains("CUTSCENE")) {
            Cutscene.create(event.getPlayer(), true)
                    .createPoint(40, 0)
                    .setTarget(event.getPlayer())
                    .addFacingRelativeVecPoint(new Vec3(5.0F, 0, 0), new Vec3(0.3F, 0, 0), EasingFunctions.LINEAR)
                    .newPoint(50, 0)
                    .setTarget(event.getPlayer())
                    .addFacingRelativeVecPoint(new Vec3(0, 0, 5.0F), new Vec3(0, 0, 0.3F), EasingFunctions.LINEAR)
                    .setFOV(20, 90, 10, EasingFunctions.LINEAR)
                    .setZRot(0, 160, 40, EasingFunctions.LINEAR)
                    .setBarProps(true, 0, 100, 0, 160, 0, 50, EasingFunctions.LINEAR)
                    .build();
        }
        if (event.getMessage().getString().contains("QUEST")) {
            quest = Quest.startQuest(event.getPlayer(), ExampleQuest.class);
        }
        if (event.getMessage().getString().contains("SER")) {
            quest.serializeNBT(tag);
        }
        if (event.getMessage().getString().contains("DES")) {
            Quest.startQuest(event.getPlayer(), ExampleQuest.class, tag);
        }
    }

}
