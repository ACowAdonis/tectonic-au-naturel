package dev.worldgen.tectonic.worldgen.densityfunction;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;

public record Invert(DensityFunction input, double min, double max) implements DensityFunction {
    public static final MapCodec<Invert> DATA_CODEC =  DensityFunction.HOLDER_HELPER_CODEC.fieldOf("argument").xmap(Invert::create, Invert::input);
    public static KeyDispatchDataCodec<Invert> CODEC_HOLDER = KeyDispatchDataCodec.of(DATA_CODEC);

    public static Invert create(DensityFunction input) {
        double min = input.minValue();
        double max = input.maxValue();
        if (min < 0 && max > 0) {
            return new Invert(input, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        }
        return new Invert(input, min, max);
    }

    public double transform(double d) {
        // Protect against division by zero and near-zero values
        // Use epsilon appropriate for Minecraft coordinate scales (typically |d| >> 0.001)
        // When d crosses zero, clamp to small value to avoid Infinity/NaN propagation
        if (Math.abs(d) < 1e-10) {
            return d >= 0 ? 1e10 : -1e10;
        }
        return 1.0 / d;
    }

    @Override
    public double compute(FunctionContext functionContext) {
        return this.transform(this.input().compute(functionContext));
    }

    @Override
    public void fillArray(double[] densities, DensityFunction.ContextProvider context) {
        this.input().fillArray(densities, context);

        for (int i = 0; i < densities.length; i++) {
            densities[i] = this.transform(densities[i]);
        }
    }

    @Override
    public DensityFunction mapAll(Visitor visitor) {
        return create(this.input.mapAll(visitor));
    }

    @Override
    public double minValue() {
        return this.min;
    }

    @Override
    public double maxValue() {
        return this.max;
    }

    @Override
    public KeyDispatchDataCodec<? extends DensityFunction> codec() {
        return CODEC_HOLDER;
    }
}
