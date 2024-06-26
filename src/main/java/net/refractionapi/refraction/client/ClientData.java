package net.refractionapi.refraction.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;
import net.refractionapi.refraction.cutscenes.client.ClientCutsceneData;
import net.refractionapi.refraction.examples.interaction.ExampleInteractionScreen;
import net.refractionapi.refraction.interaction.NPCInteraction;
import net.refractionapi.refraction.math.EasingFunctions;
import net.refractionapi.refraction.quest.client.ClientQuestInfo;
import net.refractionapi.refraction.sound.TrackingSound;

import java.util.function.Supplier;

public class ClientData {

    public static boolean canMove = true;
    public static boolean canRotateCamera = true;

    public static int startFOV = -1;
    public static double currentFOV = -1;
    public static int endFOV = -1;
    public static int transitionTicksFOV = -1;
    public static int progressTrackerFOV = 0;
    public static EasingFunctions easingFunctionFOV = EasingFunctions.LINEAR;

    public static float startZRot = -1;
    public static float currentZRot = -1;
    public static float endZRot = -1;
    public static int transitionTicksZRot = -1;
    public static int progressTrackerZRot = 0;
    public static EasingFunctions easingFunctionZRot = EasingFunctions.LINEAR;

    public static void trackingSound(int entityId, SoundEvent soundEvent, boolean looping, int ticks) {
        LivingEntity livingEntity = (LivingEntity) Minecraft.getInstance().level.getEntity(entityId);
        if (livingEntity != null) {
            TrackingSound sound = new TrackingSound(soundEvent, livingEntity, looping, ticks);
            Minecraft.getInstance().getSoundManager().play(sound);
        }
    }

    public static void playLocalSound(ResourceLocation resourceLocation) {
        SoundEvent event = ForgeRegistries.SOUND_EVENTS.getValue(resourceLocation);
        if (event == null) return;
        Minecraft.getInstance().getSoundManager().play(new EntityBoundSoundInstance(event, SoundSource.AMBIENT, 1.0F, 1.0F, Minecraft.getInstance().player, RandomSource.create().nextLong()));
    }

    public static Player getPlayer() {
        return Minecraft.getInstance().player;
    }

    public static void handleInteraction(Supplier<NPCInteraction> interaction, CompoundTag tag) {
        String stage = tag.getString("stage");
        boolean close = tag.getBoolean("close");
        if (Minecraft.getInstance().screen instanceof ExampleInteractionScreen screen) {
            if (!stage.isEmpty()) screen.switchStage(stage);
            if (close) screen.onClose();
            return;
        }
        Minecraft.getInstance().setScreen(new ExampleInteractionScreen(interaction.get()));
    }

    public static void reset() {
        canMove = true;
        canRotateCamera = true;
        startFOV = -1;
        currentFOV = -1;
        endFOV = -1;
        transitionTicksFOV = -1;
        progressTrackerFOV = 0;
        easingFunctionFOV = EasingFunctions.LINEAR;
        startZRot = -1;
        currentZRot = -1;
        endZRot = -1;
        transitionTicksZRot = -1;
        progressTrackerZRot = 0;
        easingFunctionZRot = EasingFunctions.LINEAR;
        ClientCutsceneData.reset();
        ClientQuestInfo.reset();
    }

}
