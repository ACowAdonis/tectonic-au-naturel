package dev.worldgen.tectonic;

import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.RepositorySource;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TectonicRepositorySource implements RepositorySource {
    public static final List<Pack> PACKS = new ArrayList<>();

    @Override
    public void loadPacks(@NotNull Consumer<Pack> consumer) {
        PACKS.forEach(consumer);
    }
}