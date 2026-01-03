package dev.worldgen.tectonic.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import com.mojang.serialization.JsonOps;
import dev.worldgen.tectonic.Tectonic;
import dev.worldgen.tectonic.config.state.ConfigState;
import net.minecraft.util.GsonHelper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Optional;

public class ConfigHandler {
    // Volatile ensures visibility across chunk generation threads (important for C2ME compatibility)
    private static volatile ConfigState LOADED_STATE = new ConfigState(
        ConfigState.MINOR_VERSION,
        ConfigState.General.DEFAULT,
        ConfigState.GlobalTerrain.DEFAULT,
        ConfigState.Continents.DEFAULT,
        ConfigState.Islands.DEFAULT,
        ConfigState.Oceans.DEFAULT,
        ConfigState.Biomes.DEFAULT,
        ConfigState.Caves.DEFAULT
    );
    private static Path PATH;

    public static ConfigState getState() {
        return LOADED_STATE;
    }

    public static void setState(ConfigState state) {
        LOADED_STATE = state;
    }

    public static void load(Path path) {
        PATH = path;

        if (!Files.isRegularFile(path)) {
            save();
        }

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            JsonElement json = JsonParser.parseReader(reader);
            Optional<ConfigState> result = ConfigState.CODEC.parse(JsonOps.INSTANCE, json).result();
            if (result.isPresent()) {
                LOADED_STATE = result.get();
            } else {
                throw new JsonParseException("Invalid codec");
            }
        } catch (JsonParseException e) {
            Tectonic.LOGGER.error("Couldn't parse config file, resetting to default config");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        save();
    }

    public static void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(PATH)) {
            JsonElement element = ConfigState.CODEC.encodeStart(JsonOps.INSTANCE, LOADED_STATE).result().get();
            StringWriter stringWriter = new StringWriter();
            JsonWriter jsonWriter = new JsonWriter(stringWriter);
            jsonWriter.setIndent("  ");
            GsonHelper.writeValue(jsonWriter, element, Comparator.naturalOrder());
            writer.write(stringWriter.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}