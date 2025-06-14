package net.refractionapi.refraction.helper.vfx;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class EmbeddedParticleOptions implements ParticleOptions {
    private final ParticleType<?> particle;
    public BiConsumer<Object, Float> renderConsumer = (particle, partial) -> {
    };
    public Consumer<Object> tickConsumer = null;

    public EmbeddedParticleOptions(ParticleType<?> particle) {
        this.particle = particle;
    }

    public EmbeddedParticleOptions onRender(BiConsumer<Object, Float> particleConsumer) {
        this.renderConsumer = particleConsumer;
        return this;
    }

    public EmbeddedParticleOptions onTick(Consumer<Object> tickConsumer) {
        this.tickConsumer = tickConsumer;
        return this;
    }

    public static MapCodec<EmbeddedParticleOptions> codec(ParticleType<EmbeddedParticleOptions> options) {
        return ExtraCodecs.ARGB_COLOR_CODEC.xmap(ignored -> new EmbeddedParticleOptions(options), ignored -> 0).fieldOf("ignored");
    }

    public static StreamCodec<? super ByteBuf, EmbeddedParticleOptions> streamCodec(ParticleType<EmbeddedParticleOptions> type) {
        return ByteBufCodecs.INT.map(ignored -> new EmbeddedParticleOptions(type), ignored -> 0);
    }

    @Override
    public ParticleType<?> getType() {
        return particle;
    }
}
