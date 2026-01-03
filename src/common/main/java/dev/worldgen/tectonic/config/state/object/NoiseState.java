package dev.worldgen.tectonic.config.state.object;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class NoiseState {
    public static final NoiseState DEFAULT = new NoiseState(0.25, 1, 0);
    public double scale;
    public double multiplier;
    public double offset;

    public NoiseState(double scale, double multiplier, double offset) {
        this.scale = scale;
        this.multiplier = multiplier;
        this.offset = offset;
    }

    public static MapCodec<NoiseState> codec(String name) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.DOUBLE.fieldOf(name + "_scale").forGetter(state -> state.scale),
            Codec.DOUBLE.fieldOf(name + "_multiplier").forGetter(state -> state.multiplier),
            Codec.DOUBLE.fieldOf(name + "_offset").forGetter(state -> state.offset)
        ).apply(instance, NoiseState::new));
    }
}
