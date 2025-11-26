package net.refractionapi.refraction.feature.subdivision;

import net.minecraft.resources.ResourceLocation;
import net.refractionapi.refraction.helper.randomizer.WeightedRandom;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class SubdivisionSet {
    private WeightedRandom<SubdivisionPiece.Configurer> pieces = new WeightedRandom<>();
    @Nullable
    private SubdivisionPiece.Configurer origin = null;
    protected int maxDepth = 5;

    public SubdivisionSet() {

    }

    public SubdivisionPiece.Configurer configurer(ResourceLocation id) {
        return new SubdivisionPiece.Configurer(id);
    }

    public SubdivisionSet maxDepth(int depth) {
        this.maxDepth = depth;
        return this;
    }

    public SubdivisionSet setOrigin(ResourceLocation id, Consumer<SubdivisionPiece.Configurer> consumer) {
        var config = configurer(id);
        consumer.accept(config);
        this.origin = config;
        return this.add(config);
    }

    public SubdivisionSet add(ResourceLocation id, Consumer<SubdivisionPiece.Configurer> consumer) {
        var config = configurer(id);
        consumer.accept(config);
        return this.add(config);
    }

    public SubdivisionSet add(SubdivisionPiece.Configurer config) {
        this.pieces.add(config, config.weight);
        return this;
    }

    public WeightedRandom<SubdivisionPiece.Configurer> pieces() {
        return pieces;
    }

    public SubdivisionPiece.Configurer origin() {
        return origin == null ? pieces.get() : origin;
    }
}
