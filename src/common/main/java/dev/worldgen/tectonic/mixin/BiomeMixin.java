package dev.worldgen.tectonic.mixin;

import dev.worldgen.tectonic.config.ConfigHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.concurrent.atomic.AtomicInteger;

@Mixin(Biome.class)
public abstract class BiomeMixin {
    // Cache the snow start offset to avoid repeated config lookups
    // Uses AtomicInteger with CAS for thread-safe lazy initialization
    // CAS approach chosen over double-checked locking because:
    // - Faster under contention (no lock acquisition overhead)
    // - Simpler implementation with fewer edge cases
    // - Config field access is cheap, so redundant reads are acceptable
    private static final AtomicInteger cachedSnowStartOffset = new AtomicInteger(Integer.MIN_VALUE);

    @ModifyVariable(
        method = "getHeightAdjustedTemperature",
        at = @At("HEAD"),
        ordinal = 0,
        argsOnly = true
    )
    private BlockPos tectonic$adjustSnowStart(BlockPos pos) {
        int offset = cachedSnowStartOffset.get();
        if (offset == Integer.MIN_VALUE) {
            // Read config value
            int configValue = ConfigHandler.getState().general.snowStartOffset;
            // Attempt atomic update - if CAS fails, another thread won the race
            cachedSnowStartOffset.compareAndSet(Integer.MIN_VALUE, configValue);
            // Re-read to ensure we use the winning value
            offset = cachedSnowStartOffset.get();
        }
        return pos.below(offset);
    }
}
