package dev.worldgen.tectonic.command;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.msrandom.multiplatform.annotations.Actual;

public class TectonicCommandActual {
    @Actual
    private static ClickEvent getClickEvent(BlockPos pos) {
        return new ClickEvent.SuggestCommand("/tp @s " + pos.getX() + " ~ " + pos.getZ());
    }

    @Actual
    private static HoverEvent getHoverEvent() {
        return new HoverEvent.ShowText(Component.translatable("chat.coordinates.tooltip"));
    }
}
