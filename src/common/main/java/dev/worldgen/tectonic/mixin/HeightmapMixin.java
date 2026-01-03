package dev.worldgen.tectonic.mixin;

import net.minecraft.world.level.levelgen.Heightmap;
import org.slf4j.Logger;
import org.slf4j.helpers.NOPLogger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Heightmap.class)
public class HeightmapMixin {
    @Shadow @Mutable @Final
    private static Logger LOGGER;

    static {
        LOGGER = NOPLogger.NOP_LOGGER;
    }
}
