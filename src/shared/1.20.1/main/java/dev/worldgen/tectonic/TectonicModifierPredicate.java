package dev.worldgen.tectonic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.worldgen.modifier.predicate.ModifierPredicate;
import dev.worldgen.tectonic.config.ConfigHandler;

public record TectonicModifierPredicate(String key) implements ModifierPredicate {
    public static final Codec<TectonicModifierPredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.fieldOf("key").forGetter(TectonicModifierPredicate::key)
    ).apply(instance, TectonicModifierPredicate::new));

    @Override
    public boolean test() {
        return ConfigHandler.getState().test(this.key);
    }

    @Override
    public Codec<? extends ModifierPredicate> codec() {
        return CODEC;
    }
}
