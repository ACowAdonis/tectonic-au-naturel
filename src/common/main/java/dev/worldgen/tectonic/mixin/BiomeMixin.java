package dev.worldgen.tectonic.mixin;

import dev.worldgen.tectonic.config.ConfigHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Biome.class)
public abstract class BiomeMixin {
    // Cache the snow start offset to avoid repeated config lookups
    // Volatile ensures visibility across threads and allows config reloads to take effect
    private static volatile int cachedSnowStartOffset = Integer.MIN_VALUE;

    @ModifyVariable(
        method = "getHeightAdjustedTemperature",
        at = @At("HEAD"),
        ordinal = 0,
        argsOnly = true
    )
    private BlockPos tectonic$adjustSnowStart(BlockPos pos) {
        int offset = cachedSnowStartOffset;
        if (offset == Integer.MIN_VALUE) {
            offset = ConfigHandler.getState().general.snowStartOffset;
            cachedSnowStartOffset = offset;
        }
        return pos.below(offset);
    }
}
