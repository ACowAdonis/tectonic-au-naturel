package dev.worldgen.tectonic.config.state.object;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public final class HeightLimits {
    public static final HeightLimits DEFAULT = new HeightLimits(-64, 320);
    public static final HeightLimits INCREASED_HEIGHT = new HeightLimits(-64, 640);

    private static final MapCodec<HeightLimits> OLD_CODEC = Codec.BOOL.fieldOf("increased_height").xmap(HeightLimits::defaultLimits, limits -> limits.maxY > 320);
    private static final MapCodec<HeightLimits> NEW_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codec.INT.fieldOf("min_y").forGetter(limits -> limits.minY),
        Codec.INT.fieldOf("max_y").forGetter(limits -> limits.maxY)
    ).apply(instance, HeightLimits::new));
    public static final MapCodec<HeightLimits> FULL_CODEC = Codec.mapEither(NEW_CODEC, OLD_CODEC).xmap(either -> either.map(t -> t, t -> t), Either::left);

    public int minY;
    public int maxY;

    public HeightLimits(int minY, int maxY) {
        if (minY % 16 != 0) {
            throw new IllegalArgumentException("min_y should be a multiple of 16!");
        } else if (minY > 0) {
            throw new IllegalArgumentException("min_y should be greater than 0!");
        } else if (maxY % 16 != 0) {
            throw new IllegalArgumentException("max_y should be a multiple of 16!");
        } else if (maxY < 256) {
            throw new IllegalArgumentException("max_y should be less than 256!");
        }
        this.minY = minY;
        this.maxY = maxY;
    }

    public static HeightLimits defaultLimits(boolean increasedHeight) {
        return increasedHeight ? INCREASED_HEIGHT : DEFAULT;
    }

    public int getHeight() {
        return this.maxY - this.minY;
    }
}
