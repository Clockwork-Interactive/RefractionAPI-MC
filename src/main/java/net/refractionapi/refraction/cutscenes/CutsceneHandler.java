package net.refractionapi.refraction.cutscenes;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.refractionapi.refraction.Refraction;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

@Mod.EventBusSubscriber(modid = Refraction.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CutsceneHandler {
    public static final List<Cutscene> ACTIVE_CUTSCENES = new ArrayList<>();

    @SubscribeEvent
    public static void serverTick(TickEvent.ServerTickEvent event) {
        if (event.phase.equals(TickEvent.Phase.END)) return;

        ListIterator<Cutscene> iterator = ACTIVE_CUTSCENES.listIterator();
        while (iterator.hasNext()) {
            Cutscene cutscene = iterator.next();
            cutscene.tick();
            if (cutscene.stopped) {
                cutscene.stop();
                iterator.remove();
            }

        }
    }

    @SubscribeEvent
    public static void chat(ServerChatEvent event) { // TODO temp
        if (event.getMessage().equals("CUTSCENE")) {
            new Cutscene(event.getPlayer(), new BlockPos(event.getPlayer().position().x, event.getPlayer().position().y + 1, event.getPlayer().position().z), new int[]{40, 40, 40}, new BlockPos(0,4,0), new BlockPos(5,4,10), new BlockPos(9,4,9));
        }
    }

}
