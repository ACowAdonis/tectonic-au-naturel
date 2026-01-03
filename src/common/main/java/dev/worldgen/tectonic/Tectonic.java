package dev.worldgen.tectonic;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import dev.worldgen.tectonic.config.ConfigHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.msrandom.multiplatform.annotations.Expect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.function.Function;

public class Tectonic {
    public static final String MOD_ID = "tectonic";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    /**
     * Value saved in chunks used for blending between Tectonic versions.
     * <ol>
     *     <li>3.0.0 - 3.0.8, 3.0.10+</li>
     *     <li>3.0.9</li>
     * </ol>
     */
    public static int BLENDING_VERSION = 1;
    public static String BLENDING_KEY = "tectonic:blending_version";

    public static void init(Path folder) {
        ConfigHandler.load(folder.resolve("tectonic.json"));
    }

    @Expect
    public static ResourceLocation idVanilla(String name);

    @Expect
    public static ResourceLocation id(String name);

    @Expect
    public static int getBlendingVersion(CompoundTag tag);

    public static <T, U> Codec<T> withAlternative(final Codec<T> primary, final Codec<U> alternative, final Function<U, T> converter) {
        return Codec.either(primary, alternative).xmap(either -> either.map(v -> v, converter), Either::left);
    }

    public static boolean isEnabled() {
        return ConfigHandler.getState().general.modEnabled;
    }
}
