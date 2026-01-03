package dev.worldgen.tectonic.lithostitched;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.worldgen.modifier.Modifier;
import dev.worldgen.lithostitched.worldgen.modifier.predicate.ModifierPredicate;
import dev.worldgen.lithostitched.worldgen.modifier.predicate.TrueModifierPredicate;
import dev.worldgen.tectonic.Tectonic;
import dev.worldgen.tectonic.config.ConfigHandler;
import dev.worldgen.tectonic.config.state.object.HeightLimits;
import dev.worldgen.tectonic.mixin.DimensionTypeAccessor;
import dev.worldgen.tectonic.mixin.NoiseSettingsAccessor;
import net.minecraft.core.Holder;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

public record SetHeightLimitsModifier(Holder<DimensionType> dimensionType, Holder<NoiseGeneratorSettings> noiseSettings) implements Modifier {
    public static final Codec<SetHeightLimitsModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        DimensionType.CODEC.fieldOf("dimension_type").forGetter(SetHeightLimitsModifier::dimensionType),
        NoiseGeneratorSettings.CODEC.fieldOf("noise_settings").forGetter(SetHeightLimitsModifier::noiseSettings)
    ).apply(instance, SetHeightLimitsModifier::new));

    @Override
    public ModifierPredicate getPredicate() {
        return TrueModifierPredicate.INSTANCE;
    }

    @Override
    public ModifierPhase getPhase() {
        return ModifierPhase.AFTER_ALL;
    }

    @Override
    public void applyModifier() {
        if (!Tectonic.isEnabled()) return;

        HeightLimits limits = ConfigHandler.getState().globalTerrain.heightLimits;

        DimensionTypeAccessor typeAccessor = (DimensionTypeAccessor) (Object) this.dimensionType.value();
        typeAccessor.setMinY(limits.minY);
        typeAccessor.setHeight(limits.getHeight());
        typeAccessor.setLogicalHeight(limits.getHeight());

        NoiseSettingsAccessor settingsAccessor = (NoiseSettingsAccessor) (Object) this.noiseSettings.value().noiseSettings();
        settingsAccessor.setMinY(limits.minY);
        settingsAccessor.setHeight(limits.getHeight());
    }

    @Override
    public Codec<? extends Modifier> codec() {
        return CODEC;
    }
}
