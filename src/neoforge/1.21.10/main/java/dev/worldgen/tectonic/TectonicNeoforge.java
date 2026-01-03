package dev.worldgen.tectonic;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.registry.LithostitchedRegistryKeys;
import dev.worldgen.tectonic.command.TectonicCommand;
import dev.worldgen.tectonic.config.ConfigHandler;
import dev.worldgen.tectonic.lithostitched.SetHeightLimitsModifier;
import dev.worldgen.tectonic.worldgen.densityfunction.ConfigClamp;
import dev.worldgen.tectonic.worldgen.densityfunction.ConfigConstant;
import dev.worldgen.tectonic.worldgen.densityfunction.ConfigNoise;
import dev.worldgen.tectonic.worldgen.densityfunction.Invert;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.*;
import net.minecraft.server.packs.repository.BuiltInPackSource;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.jarcontents.JarContents;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.resource.JarContentsPackResources;
import net.neoforged.neoforgespi.language.IModInfo;
import org.apache.maven.artifact.versioning.ArtifactVersion;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static dev.worldgen.tectonic.Tectonic.id;

@Mod(Tectonic.MOD_ID)
public class TectonicNeoforge {
    public static final DeferredRegister<MapCodec<? extends ICondition>> CONDITION_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.CONDITION_CODECS, Tectonic.MOD_ID);
    public static final DeferredHolder<MapCodec<? extends ICondition>, MapCodec<ConfigResourceCondition>> TECTONIC = CONDITION_TYPES.register("config", () -> ConfigResourceCondition.CODEC);

    public TectonicNeoforge(IEventBus bus) {
        Tectonic.init(FMLPaths.CONFIGDIR.get());

        CONDITION_TYPES.register(bus);

        bus.addListener(this::registerDensityFunctionTypes);
        bus.addListener(this::registerEnabledPacks);
        NeoForge.EVENT_BUS.addListener(this::registerCommands);
    }

    private void registerDensityFunctionTypes(final RegisterEvent event) {
        event.register(Registries.DENSITY_FUNCTION_TYPE, helper -> {
            helper.register(id("config_clamp"), ConfigClamp.CODEC_HOLDER.codec());
            helper.register(id("config_constant"), ConfigConstant.CODEC_HOLDER.codec());
            helper.register(id("config_noise"), ConfigNoise.CODEC_HOLDER.codec());
            helper.register(id("invert"), Invert.CODEC_HOLDER.codec());
        });
        event.register(LithostitchedRegistryKeys.MODIFIER_TYPE, helper -> {
            helper.register(id("set_height_limits"), SetHeightLimitsModifier.CODEC);
        });
    }

    private void registerEnabledPacks(final AddPackFindersEvent event) {
        if (ConfigHandler.getState().general.modEnabled) {
            addPackFinders(
                event,
                ResourceLocation.fromNamespaceAndPath(Tectonic.MOD_ID, "resourcepacks/tectonic"),
                PackType.SERVER_DATA,
                Component.literal("Tectonic"),
                PackSource.BUILT_IN,
                true,
                Pack.Position.TOP
            );
        }
    }

    // Patched version of AddPackFindersEvent#addPackFinders while waiting for Neoforge issue #2724 to be fixed.
    public static void addPackFinders(AddPackFindersEvent event, ResourceLocation location, PackType type, Component name, PackSource source, boolean alwaysActive, Pack.Position position) {
        if (event.getPackType() == type) {
            IModInfo modInfo = (ModList.get().getModContainerById(location.getNamespace()).orElseThrow(() -> new IllegalArgumentException("Mod not found: " + location.getNamespace()))).getModInfo();
            ArtifactVersion version = modInfo.getVersion();
            BiFunction<PackLocationInfo, String, PackResources> resourceGetter = (info, prefix) -> {
                JarContents contents = modInfo.getOwningFile().getFile().getContents();
                return new JarContentsPackResources(info, contents, prefix);
            };
            String path = location.getPath();

            Pack pack = Pack.readMetaAndCreate(
                new PackLocationInfo("mod/" + location, name, source, Optional.of(new KnownPack("neoforge", "mod/" + location, version.toString()))),
                new Pack.ResourcesSupplier() {
                    @Override
                    public PackResources openPrimary(PackLocationInfo info) {
                        return resourceGetter.apply(info, path);
                    }

                    @Override
                    public PackResources openFull(PackLocationInfo info, Pack.Metadata metadata) {
                        PackResources baseResources = resourceGetter.apply(info, path);
                        List<String> overlays = metadata.overlays();
                        if (overlays.isEmpty()) {
                            return baseResources;
                        } else {
                            List<PackResources> effectiveOverlays = new ArrayList(overlays.size());

                            for(String s : overlays) {
                                effectiveOverlays.add(resourceGetter.apply(info, path + "/" + s));
                            }

                            return new CompositePackResources(baseResources, effectiveOverlays);
                        }
                    }
                },
                type,
                new PackSelectionConfig(alwaysActive, position, false)
            );
            event.addRepositorySource(consumer -> consumer.accept(pack));
        }
    }

    private void registerCommands(final RegisterCommandsEvent event) {
        TectonicCommand.register(event.getDispatcher());
    }
}