package dev.worldgen.tectonic.mixin;

import net.minecraft.world.level.levelgen.NoiseSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(NoiseSettings.class)
public interface NoiseSettingsAccessor {
    @Accessor("minY") @Mutable
    void setMinY(int minY);

    @Accessor("height") @Mutable
    void setHeight(int height);
}
