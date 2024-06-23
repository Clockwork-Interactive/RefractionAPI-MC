package net.refractionapi.refraction.event.events;

import net.minecraft.client.gui.LayeredDraw;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

public class RegisterGuiEvent extends Event implements IModBusEvent {

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
