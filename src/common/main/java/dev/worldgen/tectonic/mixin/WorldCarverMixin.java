package dev.worldgen.tectonic.mixin;

import net.minecraft.world.level.levelgen.carver.WorldCarver;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(WorldCarver.class)
public class WorldCarverMixin {
    // TODO: Look into this again with some sort of cave_entrances config option
    /*@Inject(method = "canReplaceBlock", at = @At("HEAD"), cancellable = true)
    private <C extends CarverConfiguration> void init(C config, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (state.is(BlockTags.DIRT)) {
            cir.setReturnValue(false);
        }
    }*/
}
