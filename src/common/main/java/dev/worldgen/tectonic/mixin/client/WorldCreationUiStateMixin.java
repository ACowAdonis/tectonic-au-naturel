package dev.worldgen.tectonic.mixin.client;

import dev.worldgen.tectonic.Tectonic;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(WorldCreationUiState.class)
public class WorldCreationUiStateMixin {
    @Unique private static final TagKey<WorldPreset> INCOMPATIBLE = TagKey.create(Registries.WORLD_PRESET, Tectonic.id("incompatible"));

    @Inject(
        method = "getNonEmptyList",
        at = @At("RETURN"),
        cancellable = true
    )
    private static void tectonic$removeUnusablePresets(Registry<WorldPreset> registry, TagKey<WorldPreset> tag, CallbackInfoReturnable<Optional<List<WorldCreationUiState.WorldTypeEntry>>> cir) {
        if (cir.getReturnValue().isPresent()) {
            var list = new ArrayList<>(cir.getReturnValue().get());
            list.removeIf(entry -> entry.preset() != null && entry.preset().is(INCOMPATIBLE));
            cir.setReturnValue(Optional.of(list));
        }
    }
}
