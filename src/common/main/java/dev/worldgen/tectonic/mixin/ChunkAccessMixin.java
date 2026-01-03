package dev.worldgen.tectonic.mixin;

import it.unimi.dsi.fastutil.shorts.ShortList;
import it.unimi.dsi.fastutil.shorts.ShortLists;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkAccess.class)
public class ChunkAccessMixin {
    // Cached empty list to avoid allocation on every invalid index access
    @Unique
    private static final ShortList EMPTY_LIST = ShortLists.emptyList();

    @Inject(
        method = "getOrCreateOffsetList",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void tectonic$stopHeightDecreasingLogSpam(ShortList[] positions, int index, CallbackInfoReturnable<ShortList> cir) {
        if (index >= positions.length) {
            cir.setReturnValue(EMPTY_LIST);
        }
    }
}
