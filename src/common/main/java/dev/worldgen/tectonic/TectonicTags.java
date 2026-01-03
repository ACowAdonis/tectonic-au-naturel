package dev.worldgen.tectonic;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public interface TectonicTags {
    TagKey<Block> TERRALITH_SURFACE = TagKey.create(Registries.BLOCK, Tectonic.id("terralith_surface"));
}
