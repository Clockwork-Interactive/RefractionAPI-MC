package net.refractionapi.refraction.capabilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.refractionapi.refraction.Refraction;

public class CapabilityUtil {

    public static <T extends Data<T>> void playerClone(PlayerEvent.Clone event, Capability<T> data) {
        event.getOriginal().reviveCaps();
        event.getOriginal().getCapability(data).ifPresent(oldStore -> event.getEntity().getCapability(data).ifPresent(newStore -> newStore.copyFromData(oldStore)));
        event.getOriginal().invalidateCaps();
    }

    public static void attachCapability(AttachCapabilitiesEvent<? extends CapabilityProvider<?>> event, ICapabilityProvider provider, Capability<?> capability) {
        if (!event.getObject().getCapability(capability).isPresent()) {
            event.addCapability(new ResourceLocation(Refraction.MOD_ID, provider.getClass().getSimpleName().toLowerCase()), provider);
        }
    }

}
