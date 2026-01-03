package dev.worldgen.tectonic.lithostitched;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.mixin.common.MappedRegistryAccessor;
import dev.worldgen.lithostitched.worldgen.modifier.Modifier;
import dev.worldgen.tectonic.Tectonic;
import dev.worldgen.tectonic.config.ConfigHandler;
import dev.worldgen.tectonic.config.state.object.HeightLimits;
import dev.worldgen.tectonic.mixin.DimensionTypeAccessor;
import dev.worldgen.tectonic.mixin.NoiseSettingsAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

import java.util.Optional;

public record SetHeightLimitsModifier(int priority, Holder<DimensionType> dimensionType, Holder<NoiseGeneratorSettings> noiseSettings) implements Modifier {
    public static final MapCodec<SetHeightLimitsModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Modifier.PRIORITY_DEFAULT.forGetter(SetHeightLimitsModifier::priority),
        DimensionType.CODEC.fieldOf("dimension_type").forGetter(SetHeightLimitsModifier::dimensionType),
        NoiseGeneratorSettings.CODEC.fieldOf("noise_settings").forGetter(SetHeightLimitsModifier::noiseSettings)
    ).apply(instance, SetHeightLimitsModifier::new));

    @Override
    public void applyModifier(RegistryAccess registries) {
        if (!Tectonic.isEnabled()) return;

        HeightLimits limits = ConfigHandler.getState().globalTerrain.heightLimits;

        DimensionTypeAccessor typeAccessor = (DimensionTypeAccessor) (Object) this.dimensionType.value();
        typeAccessor.setMinY(limits.minY);
        typeAccessor.setHeight(limits.getHeight());
        typeAccessor.setLogicalHeight(limits.getHeight());

        NoiseSettingsAccessor settingsAccessor = (NoiseSettingsAccessor) (Object) this.noiseSettings.value().noiseSettings();
        settingsAccessor.setMinY(limits.minY);
        settingsAccessor.setHeight(limits.getHeight());

        // Ensure dimension type is synced
        if (this.dimensionType.unwrapKey().isPresent()) {
            Registry<DimensionType> registry = registries.registryOrThrow(Registries.DIMENSION_TYPE);
            ResourceKey<DimensionType> key = this.dimensionType.unwrapKey().get();
            Optional<RegistrationInfo> knownPackInfo = registry.registrationInfo(key);
            knownPackInfo.ifPresent(registrationInfo -> ((MappedRegistryAccessor<DimensionType>)registry).lithostitched$getRegistrationInfos().put(key, new RegistrationInfo(Optional.empty(), registrationInfo.lifecycle())));
        }
    }

    @Override
    public void applyModifier() {

    }

    @Override
    public int priority() {
        return 0;
    }

    @Override
    public MapCodec<SetHeightLimitsModifier> codec() {
        return CODEC;
    }
}
