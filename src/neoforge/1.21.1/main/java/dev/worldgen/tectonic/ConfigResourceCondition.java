package dev.worldgen.tectonic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.tectonic.config.ConfigHandler;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jetbrains.annotations.NotNull;

public record ConfigResourceCondition(String key) implements ICondition {
    public static final MapCodec<ConfigResourceCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codec.STRING.fieldOf("key").forGetter(ConfigResourceCondition::key)
    ).apply(instance, ConfigResourceCondition::new));

    @Override
    public boolean test(@NotNull IContext context) {
        return ConfigHandler.getState().test(this.key);
    }

    @Override
    public @NotNull MapCodec<? extends ICondition> codec() {
        return CODEC;
    }
}