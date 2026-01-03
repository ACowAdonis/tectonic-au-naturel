package dev.worldgen.tectonic.client;

import dev.worldgen.tectonic.config.ConfigHandler;
import dev.worldgen.tectonic.config.state.ConfigState;
import dev.worldgen.tectonic.config.state.object.NoiseState;
import net.minecraft.client.gui.Font;

import java.util.function.Consumer;

import static dev.worldgen.tectonic.config.state.ConfigState.Caves.*;
import static dev.worldgen.tectonic.config.state.ConfigState.Continents.*;
import static dev.worldgen.tectonic.config.state.ConfigState.General.*;
import static dev.worldgen.tectonic.config.state.ConfigState.GlobalTerrain.*;
import static dev.worldgen.tectonic.config.state.ConfigState.Islands.*;
import static dev.worldgen.tectonic.config.state.ConfigState.Oceans.*;

public interface ConfigListBuilder {
    void addCategory(String name, Font font);
    void addBoolean(String name, Consumer<Boolean> setter, boolean getter, boolean defaultValue);
    void addInteger(String name, double min, double max, double step, Consumer<Integer> setter, double getter, double defaultValue);
    void addDouble(String name, double min, double max, double step, Consumer<Double> setter, double getter, double defaultValue);
    void addNoise(String name, NoiseState state, NoiseState defaultState);

    default void build(Font font) {
        ConfigState state = ConfigHandler.getState();

        this.addCategory("general", font);
        this.addBoolean("mod_enabled", bool -> state.general.modEnabled = bool, state.general.modEnabled, MOD_ENABLED);
        this.addInteger("snow_start_offset", 0, 256, 1, value -> state.general.snowStartOffset = value, state.general.snowStartOffset, SNOW_START_OFFSET);

        this.addCategory("global_terrain", font);
        this.addDouble("vertical_scale", 0.75, 15, 0.005, value -> state.globalTerrain.verticalScale = value, state.globalTerrain.verticalScale, VERTICAL_SCALE);
        this.addDouble("elevation_boost", 0, 1, 0.01, value -> state.globalTerrain.elevationBoost = value, state.globalTerrain.elevationBoost, ELEVATION_BOOST);
        this.addInteger("min_y", -2032, -64, 16, value -> state.globalTerrain.heightLimits.minY = value, state.globalTerrain.heightLimits.minY, HEIGHT_LIMITS.minY);
        this.addInteger("max_y", 256, 2032, 16, value -> state.globalTerrain.heightLimits.maxY = value, state.globalTerrain.heightLimits.maxY, HEIGHT_LIMITS.maxY);
        this.addBoolean("ultrasmooth", bool -> state.globalTerrain.ultrasmooth = bool, state.globalTerrain.ultrasmooth, ULTRASMOOTH);

        this.addCategory("continents", font);
        this.addDouble("ocean_offset", -2, 0, 0.01, value -> state.continents.oceanOffset = value, state.continents.oceanOffset, OCEAN_OFFSET);
        this.addDouble("continents_scale", 0.01, 1, 0.01, value -> state.continents.continentsScale = value, state.continents.continentsScale, CONTINENTS_SCALE);
        this.addDouble("erosion_scale", 0.01, 1, 0.01, value -> state.continents.erosionScale = value, state.continents.erosionScale, EROSION_SCALE);
        this.addDouble("ridge_scale", 0.01, 2, 0.01, value -> state.continents.ridgeScale = value, state.continents.ridgeScale, RIDGE_SCALE);
        this.addBoolean("underground_rivers", bool -> state.continents.undergroundRivers = bool, state.continents.undergroundRivers, UNDERGROUND_RIVERS);
        this.addBoolean("river_lanterns", bool -> state.continents.riverLanterns = bool, state.continents.riverLanterns, RIVER_LANTERNS);
        this.addDouble("flat_terrain_skew", -1, 1, 0.01, value -> state.continents.flatTerrainSkew = value, state.continents.flatTerrainSkew, FLAT_TERRAIN_SKEW);
        this.addBoolean("rolling_hills", bool -> state.continents.rollingHills = bool, state.continents.rollingHills, ROLLING_HILLS);
        this.addBoolean("jungle_pillars", bool -> state.continents.junglePillars = bool, state.continents.junglePillars, JUNGLE_PILLARS);

        this.addCategory("islands", font);
        this.addBoolean("enabled", bool -> state.islands.enabled = bool,  state.islands.enabled, ENABLED);
        this.addNoise("noise", state.islands.noise, NOISE);

        this.addCategory("oceans", font);
        this.addDouble("ocean_depth", -10, -0.05, 0.01, value -> state.oceans.oceanDepth = value, state.oceans.oceanDepth, OCEAN_DEPTH);
        this.addDouble("deep_ocean_depth", -10, -0.05, 0.01, value -> state.oceans.deepOceanDepth = value, state.oceans.deepOceanDepth, DEEP_OCEAN_DEPTH);
        this.addInteger("monument_offset", -60, 0, 1, value -> state.oceans.monumentOffset = value, state.oceans.monumentOffset, MONUMENT_OFFSET);
        this.addBoolean("remove_frozen_ocean_ice", bool -> state.oceans.removeFrozenOceanIce = bool, state.oceans.removeFrozenOceanIce, REMOVE_FROZEN_OCEAN_ICE);

        this.addCategory("biomes", font);
        this.addNoise("temperature", state.biomes.temperature, NoiseState.DEFAULT);
        this.addNoise("vegetation", state.biomes.vegetation, NoiseState.DEFAULT);

        this.addCategory("caves", font);
        this.addDouble("depth_cutoff_start", -0.1, 1, 0.1, value -> state.caves.depthCutoffStart = value, state.caves.depthCutoffStart, DEPTH_CUTOFF_START);
        this.addDouble("depth_cutoff_size", 0, 1, 0.1, value -> state.caves.depthCutoffSize = value, state.caves.depthCutoffSize, DEPTH_CUTOFF_SIZE);
        this.addBoolean("cheese_enabled", bool -> state.caves.cheeseEnabled = bool, state.caves.cheeseEnabled, CHEESE_ENABLED);
        this.addDouble("cheese_additive", -0.5, 0.5, 0.01, value -> state.caves.cheeseAdditive = value, state.caves.cheeseAdditive, CHEESE_ADDITIVE);
        this.addBoolean("noodle_enabled", bool -> state.caves.noodleEnabled = bool, state.caves.noodleEnabled, NOODLE_ENABLED);
        this.addDouble("noodle_additive", -0.25, 0.25, 0.025, value -> state.caves.noodleAdditive = value, state.caves.noodleAdditive, NOODLE_ADDITIVE);
        this.addBoolean("spaghetti_enabled", bool -> state.caves.spaghettiEnabled = bool, state.caves.spaghettiEnabled, SPAGHETTI_ENABLED);
        this.addBoolean("carvers_enabled", bool -> state.caves.carversEnabled = bool, state.caves.carversEnabled, CARVERS_ENABLED);
        this.addBoolean("lava_tunnels", bool -> state.globalTerrain.lavaTunnels = bool, state.globalTerrain.lavaTunnels, LAVA_TUNNELS);

    }
}
