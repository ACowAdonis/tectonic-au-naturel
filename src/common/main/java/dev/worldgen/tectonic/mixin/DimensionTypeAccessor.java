package dev.worldgen.tectonic.mixin;

import net.minecraft.world.level.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DimensionType.class)
public interface DimensionTypeAccessor {
    @Accessor("minY") @Mutable
    void setMinY(int minY);

    @Accessor("height") @Mutable
    void setHeight(int height);

    @Accessor("logicalHeight") @Mutable
    void setLogicalHeight(int logicalHeight);
}
