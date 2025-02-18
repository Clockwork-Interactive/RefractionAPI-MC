package net.refractionapi.refraction.util;

import net.minecraft.nbt.CompoundTag;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.events.RefractionClientEvents;
import net.refractionapi.refraction.helper.misc.TagIO;

public class TestHooksClient {
    private final TagIO tagIO = new TagIO("testDir");

    public TestHooksClient() {
        this.onRenderPost();
        this.onPlayerJoin();
        this.onPlayerLeave();
    }

    public void onRenderPost() {
        RefractionClientEvents.POST_ALL.register(context -> {
        });
    }

    public void onPlayerJoin() {
        RefractionClientEvents.CLIENT_PLAYER_JOIN.register(() -> {
            CompoundTag tag = new CompoundTag();
            tag.putString("test1", "test");
            tag.putDouble("test2", 0.0D);
            this.tagIO.save("test", tag);
        });
    }

    public void onPlayerLeave() {
        RefractionClientEvents.CLIENT_PLAYER_LEAVE.register(() -> {
            CompoundTag tag = this.tagIO.load("test");
            if (tag == null) return;
            Refraction.LOGGER.info(tag.toString());
        });
    }
}
