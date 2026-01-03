package dev.worldgen.tectonic.config.state;

import dev.worldgen.tectonic.config.state.object.NoiseState;
import org.apache.logging.log4j.util.TriConsumer;

public interface ConfigPresets {
    ConfigState DEFAULT = new ConfigState(
        ConfigState.MINOR_VERSION,
        ConfigState.General.DEFAULT,
        ConfigState.GlobalTerrain.DEFAULT,
        ConfigState.Continents.DEFAULT,
        ConfigState.Islands.DEFAULT,
        ConfigState.Oceans.DEFAULT,
        ConfigState.Biomes.DEFAULT,
        ConfigState.Caves.DEFAULT
    );

    ConfigState LARGE_BIOMES = new ConfigState(
        ConfigState.MINOR_VERSION,
        ConfigState.General.DEFAULT,
        ConfigState.GlobalTerrain.DEFAULT,
        ConfigState.Continents.DEFAULT,
        ConfigState.Islands.DEFAULT,
        ConfigState.Oceans.DEFAULT,
        new ConfigState.Biomes(
            new NoiseState(0.06, 1.1, 0),
            new NoiseState(0.06, 1.1, 0)
        ),
        ConfigState.Caves.DEFAULT
    );

    ConfigState DESERTED = new ConfigState(
        ConfigState.MINOR_VERSION,
        ConfigState.General.DEFAULT,
        ConfigState.GlobalTerrain.DEFAULT,
        ConfigState.Continents.DEFAULT,
        ConfigState.Islands.DEFAULT,
        ConfigState.Oceans.DEFAULT,
        new ConfigState.Biomes(
            new NoiseState(0, 0, 1),
            NoiseState.DEFAULT
        ),
        ConfigState.Caves.DEFAULT
    );

    static void acceptPresets(TriConsumer<String, ConfigState, Integer> consumer) {
        consumer.accept("default", ConfigPresets.DEFAULT, 0xffffff);
        consumer.accept("large_biomes", ConfigPresets.LARGE_BIOMES, 0x88ff99);
        consumer.accept("deserted", ConfigPresets.DESERTED, 0xe2ca76);
    }
}
