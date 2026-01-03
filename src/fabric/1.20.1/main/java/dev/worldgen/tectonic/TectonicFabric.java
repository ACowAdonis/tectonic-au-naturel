package dev.worldgen.tectonic;

import dev.worldgen.lithostitched.registry.LithostitchedBuiltInRegistries;
import dev.worldgen.tectonic.command.TectonicCommand;
import dev.worldgen.tectonic.config.ConfigHandler;
import dev.worldgen.tectonic.lithostitched.SetHeightLimitsModifier;
import dev.worldgen.tectonic.worldgen.densityfunction.ConfigClamp;
import dev.worldgen.tectonic.worldgen.densityfunction.ConfigConstant;
import dev.worldgen.tectonic.worldgen.densityfunction.ConfigNoise;
import dev.worldgen.tectonic.worldgen.densityfunction.Invert;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;

import java.nio.file.Path;

import static dev.worldgen.tectonic.Tectonic.id;

public class TectonicFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Tectonic.init(FabricLoader.getInstance().getConfigDir());

        CommandRegistrationCallback.EVENT.register((dispatcher, context, selection) -> TectonicCommand.register(dispatcher));

        Registry.register(BuiltInRegistries.DENSITY_FUNCTION_TYPE, id("config_clamp"), ConfigClamp.CODEC_HOLDER.codec());
        Registry.register(BuiltInRegistries.DENSITY_FUNCTION_TYPE, id("config_constant"), ConfigConstant.CODEC_HOLDER.codec());
        Registry.register(BuiltInRegistries.DENSITY_FUNCTION_TYPE, id("config_noise"), ConfigNoise.CODEC_HOLDER.codec());
        Registry.register(BuiltInRegistries.DENSITY_FUNCTION_TYPE, id("invert"), Invert.CODEC_HOLDER.codec());

        Registry.register(LithostitchedBuiltInRegistries.MODIFIER_TYPE, id("set_height_limits"), SetHeightLimitsModifier.CODEC);

        Registry.register(LithostitchedBuiltInRegistries.MODIFIER_PREDICATE_TYPE, id("config"), TectonicModifierPredicate.CODEC);
        if (ConfigHandler.getState().general.modEnabled) {
            ResourceManagerHelper.registerBuiltinResourcePack(
                id("tectonic"),
                FabricLoader.getInstance().getModContainer("tectonic").get(),
                Component.literal("Tectonic"),
                ResourcePackActivationType.ALWAYS_ENABLED
            );

            // Loads the pack overlays as separate packs

            boolean terralith = FabricLoader.getInstance().isModLoaded("terralith");
            boolean ultrasmooth = ConfigHandler.getState().globalTerrain.ultrasmooth;
            boolean noCarvers = !ConfigHandler.getState().caves.carversEnabled;
            addPack("tectonic/overlay.mod");
            if (terralith) addPack("tectonic/overlay.terratonic");
            if (ultrasmooth) addPack("tectonic/overlay.ultrasmooth");
            if (noCarvers) addPack("tectonic/overlay.no_carvers");
        }
    }

    private static void addPack(String packName) {
        Path resourcePath = FabricLoader.getInstance().getModContainer("tectonic").get().findPath("resourcepacks/"+packName).get();
        TectonicRepositorySource.PACKS.add(Pack.readMetaAndCreate(
            "tectonic/" + packName.toLowerCase(),
            Component.translatable("pack_name.tectonic."+packName),
            false,
            string -> new PathPackResources(resourcePath.getFileName().toString(), resourcePath, false),
            PackType.SERVER_DATA,
            Pack.Position.TOP,
            PackSource.BUILT_IN
        ));
    }
}
