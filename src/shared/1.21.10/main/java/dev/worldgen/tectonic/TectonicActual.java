package dev.worldgen.tectonic;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.msrandom.multiplatform.annotations.Actual;

public class TectonicActual {
    @Actual
    public static ResourceLocation idVanilla(String name) {
        return ResourceLocation.withDefaultNamespace(name);
    }

    @Actual
    public static ResourceLocation id(String name) {
        return ResourceLocation.fromNamespaceAndPath(Tectonic.MOD_ID, name);
    }

    @Actual
    public static int getBlendingVersion(CompoundTag tag) {
        return tag.getIntOr(Tectonic.BLENDING_KEY, 0);
    }
}
