package net.refractionapi.refraction.helper.vfx;

import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.refractionapi.refraction.helper.math.ColorUtils;
import net.refractionapi.refraction.helper.math.EasingFunction;
import net.refractionapi.refraction.helper.math.EasingFunctions;
import net.refractionapi.refraction.helper.registry.RRegister;
import net.refractionapi.refraction.helper.runnable.Runnabler;
import net.refractionapi.refraction.util.Pair;

import java.util.function.BiConsumer;

public class Particler extends VFXWrapper<Particler> {
    private final RRegister<ParticlerType> particleRegister;
    private BiConsumer<Runnabler, ParticlerParticle> onTick = (runnabler, particle) -> {
    };
    private Vec3 spawn = Vec3.ZERO;
    private ParticlerParticle particlerParticle;
    private int lifetime = 0;
    private float gravity = 0.0F;

    public Particler(VFXer vfXer, RRegister<ParticlerType> particleRegister) {
        super(vfXer);
        this.particleRegister = particleRegister;
        ParticleEngine engine = Minecraft.getInstance().particleEngine;
        this.particlerParticle = (ParticlerParticle) engine.createParticle(this.particleRegister.get(), 0, 0, 0, 0, 0, 0);
    }

    public Particler onTick(BiConsumer<Runnabler, ParticlerParticle> onTick) {
        this.onTick = onTick;
        return this;
    }

    public Particler setSpawn(Vec3 spawn) {
        this.spawn = spawn;
        return this;
    }

    public Particler setLifetime(int lifetime) {
        this.lifetime = lifetime;
        return this;
    }

    public Particler setGravity(float gravity) {
        this.gravity = gravity;
        return this;
    }

    public Particler spawn(Vec3 spawn) {
        this.setSpawn(spawn);
        return this.spawn();
    }

    public Particler spawn(float x, float y, float z) {
        return this.spawn(new Vec3(x, y, z));
    }

    public Particler transparency(FloatSetting transparency) {
        this.particlerParticle.transparency = transparency;
        return this;
    }

    public Particler scale(FloatSetting scale) {
        this.particlerParticle.scale = scale;
        return this;
    }

    public Particler rotation(RotationSetting rotation) {
        RotationSetting[] rotationsClone = new RotationSetting[this.particlerParticle.rotations.length + 1];
        System.arraycopy(this.particlerParticle.rotations, 0, rotationsClone, 0, this.particlerParticle.rotations.length);
        rotationsClone[this.particlerParticle.rotations.length] = rotation;
        this.particlerParticle.rotations = rotationsClone;
        return this;
    }

    public Particler color(ColorSetting color) {
        this.particlerParticle.color = color;
        return this;
    }

    public ParticlerParticle particle() {
        return this.particlerParticle;
    }

    @Override
    public Particler spawn() {
        ParticleEngine engine = Minecraft.getInstance().particleEngine;
        if (this.particlerParticle == null)
            throw new NullPointerException("Failed to create particle %s".formatted(this.particleRegister.getId()));
        this.particlerParticle.onTick(this.onTick);
        this.particlerParticle.setLifetime(this.lifetime);
        this.particlerParticle.setGravity(this.gravity);
        engine.add(this.particlerParticle);
        this.particlerParticle.setRunnabler(
                Runnabler.createClient()
                        .delayRun(this.lifetime, (runnabler) -> this.remove())
        );
        VFXer.activeWrappers.add(this);
        return this;
    }

    @Override
    public void remove() {
        if (this.particlerParticle != null)
            this.particlerParticle.remove();
        VFXer.activeWrappers.remove(this);
    }

    public static class RotationSetting extends FloatSetting {
        private final Axis axis;

        public RotationSetting(Axis axis) {
            this.axis = axis;
        }

        public RotationSetting of(float value) {
            this.progression = Pair.of(value, value);
            return this;
        }

        public RotationSetting of(float start, float end) {
            this.progression = Pair.of(start, end);
            return this;
        }

        public RotationSetting of(float start, float end, EasingFunction easing) {
            this.progression = Pair.of(start, end);
            this.easing = easing;
            return this;
        }

        public Axis getAxis() {
            return this.axis;
        }
    }

    public static class ColorSetting extends IntegerSetting {
        public ColorSetting of(int start, int end) {
            this.progression = Pair.of(start, end);
            return this;
        }

        public ColorSetting of(int color) {
            this.progression = Pair.of(color, color);
            return this;
        }

        public ColorSetting of(int start, int end, EasingFunction easing) {
            this.progression = Pair.of(start, end);
            this.easing = easing;
            return this;
        }

        public ColorSetting of(float r1, float g1, float b1, float r2, float g2, float b2, EasingFunction function) {
            this.progression = Pair.of(Mth.color(r1, g1, b1), Mth.color(r2, g2, b2));
            this.easing = function;
            return this;
        }

        public ColorSetting of(float r1, float g1, float b1, float r2, float g2, float b2) {
            this.progression = Pair.of(Mth.color(r1, g1, b1), Mth.color(r2, g2, b2));
            return this;
        }

        @Override
        public Integer get(float delta) {
            return ColorUtils.interpolateColor(this.progression.getFirst(), this.progression.getSecond(), this.easing.getEasing(delta));
        }
    }

    public static class IntegerSetting extends Setting<Integer> {
        public IntegerSetting of(int start, int end) {
            this.progression = Pair.of(start, end);
            return this;
        }

        public IntegerSetting of(int start, int end, EasingFunction easing) {
            this.progression = Pair.of(start, end);
            this.easing = easing;
            return this;
        }

        @Override
        public Integer get(float delta) {
            return Mth.floor(Mth.lerp(this.easing.getEasing(delta), this.progression.getFirst(), this.progression.getSecond()));
        }
    }

    public static class FloatSetting extends Setting<Float> {
        public FloatSetting of(float value) {
            this.progression = Pair.of(value, value);
            return this;
        }

        public FloatSetting of(float start, float end) {
            this.progression = Pair.of(start, end);
            return this;
        }

        public FloatSetting of(float start, float end, EasingFunction easing) {
            this.progression = Pair.of(start, end);
            this.easing = easing;
            return this;
        }

        @Override
        public Float get(float delta) {
            return Mth.lerp(this.easing.getEasing(delta), this.progression.getFirst(), this.progression.getSecond());
        }
    }

    private static abstract class Setting<T> {
        protected Pair<T, T> progression;
        protected EasingFunction easing = EasingFunctions.LINEAR;

        private Setting() {

        }

        public abstract T get(float delta);
    }
}
