package net.refractionapi.refraction.datagen;

import net.minecraft.client.Minecraft;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RSoundProvider extends SoundDefinitionsProvider {
    protected final String modId;
    protected final ExistingFileHelper helper;
    protected final DeferredRegister<SoundEvent> registry;

    public RSoundProvider(PackOutput output, String modId, ExistingFileHelper helper, DeferredRegister<SoundEvent> sounds) {
        super(output, modId, helper);
        this.modId = modId;
        this.helper = helper;
        this.registry = sounds;
    }

    @Override
    public void registerSounds() {
        if (registry == null) throw new NullPointerException("Sound Registry for %s is null!".formatted(modId));
        var res = Minecraft.getInstance().getResourceManager();
        var resources = helper.getResourceStack(ResourceLocation.fromNamespaceAndPath(modId, "sounds"), PackType.CLIENT_RESOURCES);
    }
}
