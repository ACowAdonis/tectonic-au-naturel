package dev.worldgen.tectonic.mixin;

import dev.worldgen.tectonic.TectonicTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BlendingData.class)
public class BlendingDataMixin {
    @Inject(
        method = "getHeightAtXZ",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/core/BlockPos$MutableBlockPos;move(Lnet/minecraft/core/Direction;)Lnet/minecraft/core/BlockPos$MutableBlockPos;"
        ),
        locals = LocalCapture.CAPTURE_FAILHARD,
        cancellable = true
    )
    private void checkTerralithSurfaceBlocks(ChunkAccess access, int x, int z, CallbackInfoReturnable<Integer> cir, int k, int l, BlockPos.MutableBlockPos pos) {
        if (access.getBlockState(pos).is(TectonicTags.TERRALITH_SURFACE)) {
            cir.setReturnValue(pos.getY());
        }
    }
}
