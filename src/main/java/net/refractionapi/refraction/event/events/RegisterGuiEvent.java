package net.refractionapi.refraction.event.events;

import net.minecraft.client.gui.LayeredDraw;
import net.minecraftforge.eventbus.api.Event;

public class RegisterGuiEvent extends Event {

    private final LayeredDraw draw;

    public RegisterGuiEvent() {
        this.draw = new LayeredDraw();
    }

    public void registerNew(LayeredDraw draw) {
        this.draw.add(draw, () -> true);
    }

    public LayeredDraw getDraw() {
        return draw;
    }

}
