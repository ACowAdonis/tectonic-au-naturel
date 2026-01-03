package dev.worldgen.tectonic;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.msrandom.multiplatform.annotations.Actual;

public class TectonicActual {
    @Actual
    public static ResourceLocation idVanilla(String name) {
        return new ResourceLocation(name);
    }

    @Actual
    public static ResourceLocation id(String name) {
        return new ResourceLocation(Tectonic.MOD_ID, name);
    }

    @Actual
    public static int getBlendingVersion(CompoundTag tag) {
        return tag.getInt(Tectonic.BLENDING_KEY);
    }
}
