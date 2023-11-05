package net.refractionapi.refraction.cutscenes;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.LogicalSide;
import net.refractionapi.refraction.cutscenes.Cutscene;

public class CutsceneTickEvent extends TickEvent {

    private final Cutscene cutscene;

    public CutsceneTickEvent(Phase phase, Cutscene cutscene) {
        super(Type.SERVER, LogicalSide.SERVER, phase);
        this.cutscene = cutscene;
    }

    public Cutscene getCutscene() {
        return cutscene;
    }

    public Player getPlayer() {
        return cutscene.player;
    }

}
