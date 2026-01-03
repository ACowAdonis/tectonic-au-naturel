package dev.worldgen.tectonic.worldgen.densityfunction;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;

public final class ConfigClamp implements DensityFunction {
    public static final MapCodec<ConfigClamp> DATA_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        DensityFunction.HOLDER_HELPER_CODEC.fieldOf("input").forGetter(ConfigClamp::input),
        DensityFunction.HOLDER_HELPER_CODEC.fieldOf("min").forGetter(ConfigClamp::min),
        DensityFunction.HOLDER_HELPER_CODEC.fieldOf("max").forGetter(ConfigClamp::max)
    ).apply(instance, ConfigClamp::new));
    public static KeyDispatchDataCodec<ConfigClamp> CODEC_HOLDER = KeyDispatchDataCodec.of(DATA_CODEC);

    // ThreadLocal array pools to avoid allocation in fillArray
    // Arrays grow as needed to accommodate larger batch sizes
    private static final ThreadLocal<double[]> MIN_ARRAY_POOL = ThreadLocal.withInitial(() -> new double[0]);
    private static final ThreadLocal<double[]> MAX_ARRAY_POOL = ThreadLocal.withInitial(() -> new double[0]);

    private final DensityFunction input;
    private final DensityFunction min;
    private final DensityFunction max;

    // Cached values for constant min/max
    private final boolean minIsConstant;
    private final boolean maxIsConstant;
    private final double cachedMin;
    private final double cachedMax;

    public ConfigClamp(DensityFunction input, DensityFunction min, DensityFunction max) {
        this.input = input;
        this.min = min;
        this.max = max;

        // Detect if min/max are constants (minValue == maxValue means it's constant)
        this.minIsConstant = min.minValue() == min.maxValue();
        this.maxIsConstant = max.minValue() == max.maxValue();
        this.cachedMin = minIsConstant ? min.minValue() : 0;
        this.cachedMax = maxIsConstant ? max.maxValue() : 0;
    }

    public DensityFunction input() {
        return input;
    }

    public DensityFunction min() {
        return min;
    }

    public DensityFunction max() {
        return max;
    }

    @Override
    public double compute(FunctionContext context) {
        double minVal = minIsConstant ? cachedMin : min.compute(context);
        double maxVal = maxIsConstant ? cachedMax : max.compute(context);
        return Math.min(Math.max(this.input.compute(context), minVal), maxVal);
    }

    @Override
    public void fillArray(double[] densities, ContextProvider context) {
        // Batch process the input
        input.fillArray(densities, context);

        if (minIsConstant && maxIsConstant) {
            // Fast path: both min and max are constants
            for (int i = 0; i < densities.length; i++) {
                densities[i] = Math.min(Math.max(densities[i], cachedMin), cachedMax);
            }
        } else {
            // Slower path: need to compute min/max per sample
            // Use pooled arrays to avoid allocation
            double[] minValues = getPooledArray(MIN_ARRAY_POOL, densities.length);
            double[] maxValues = getPooledArray(MAX_ARRAY_POOL, densities.length);

            if (!minIsConstant) {
                min.fillArray(minValues, context);
            }
            if (!maxIsConstant) {
                max.fillArray(maxValues, context);
            }

            for (int i = 0; i < densities.length; i++) {
                double minVal = minIsConstant ? cachedMin : minValues[i];
                double maxVal = maxIsConstant ? cachedMax : maxValues[i];
                densities[i] = Math.min(Math.max(densities[i], minVal), maxVal);
            }
        }
    }

    /**
     * Gets a pooled array of at least the required size.
     * If the current pooled array is too small, allocates a new one and stores it.
     */
    private static double[] getPooledArray(ThreadLocal<double[]> pool, int requiredSize) {
        double[] array = pool.get();
        if (array.length < requiredSize) {
            array = new double[requiredSize];
            pool.set(array);
        }
        return array;
    }

    /**
     * Cleans up ThreadLocal resources for the current thread.
     * Should be called when world unloads to prevent memory accumulation.
     */
    public static void cleanupThreadLocals() {
        MIN_ARRAY_POOL.remove();
        MAX_ARRAY_POOL.remove();
    }

    @Override
    public DensityFunction mapAll(Visitor visitor) {
        return input.clamp(min.maxValue(), max.minValue()).mapAll(visitor);
    }

    @Override
    public double minValue() {
        return this.min.minValue();
    }

    @Override
    public double maxValue() {
        return this.max.maxValue();
    }

    @Override
    public KeyDispatchDataCodec<? extends DensityFunction> codec() {
        return CODEC_HOLDER;
    }
}
