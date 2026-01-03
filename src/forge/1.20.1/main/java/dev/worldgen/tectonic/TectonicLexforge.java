package dev.worldgen.tectonic;

import dev.worldgen.lithostitched.registry.LithostitchedRegistryKeys;
import dev.worldgen.tectonic.client.gui.ConfigScreen;
import dev.worldgen.tectonic.command.TectonicCommand;
import dev.worldgen.tectonic.config.ConfigHandler;
import dev.worldgen.tectonic.lithostitched.SetHeightLimitsModifier;
import dev.worldgen.tectonic.worldgen.densityfunction.ConfigClamp;
import dev.worldgen.tectonic.worldgen.densityfunction.ConfigConstant;
import dev.worldgen.tectonic.worldgen.densityfunction.ConfigNoise;
import dev.worldgen.tectonic.worldgen.densityfunction.Invert;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.RegisterEvent;

import java.nio.file.Path;

import static dev.worldgen.tectonic.Tectonic.id;

@Mod(Tectonic.MOD_ID)
public class TectonicLexforge {
    public TectonicLexforge() {
        Tectonic.init(FMLPaths.CONFIGDIR.get());

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::handleRegistries);
        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);

        if (ConfigHandler.getState().general.modEnabled) {
            // Loads the pack overlays as separate packs
            boolean terralith = ModList.get().isLoaded("terralith");
            boolean ultrasmooth = ConfigHandler.getState().globalTerrain.ultrasmooth;
            boolean noCarvers = !ConfigHandler.getState().caves.carversEnabled;
            addPack("tectonic");
            addPack("tectonic/overlay.mod");
            if (terralith) addPack("tectonic/overlay.terratonic");
            if (ultrasmooth) addPack("tectonic/overlay.ultrasmooth");
            if (noCarvers) addPack("tectonic/overlay.no_carvers");
        }
    }

    private void handleRegistries(final RegisterEvent event) {
        event.register(Registries.DENSITY_FUNCTION_TYPE, helper -> {
            helper.register(id("config_clamp"), ConfigClamp.CODEC_HOLDER.codec());
            helper.register(id("config_constant"), ConfigConstant.CODEC_HOLDER.codec());
            helper.register(id("config_noise"), ConfigNoise.CODEC_HOLDER.codec());
            helper.register(id("invert"), Invert.CODEC_HOLDER.codec());
        });
        event.register(LithostitchedRegistryKeys.MODIFIER_TYPE, helper -> {
            helper.register(id("set_height_limits"), SetHeightLimitsModifier.CODEC);
        });
        event.register(LithostitchedRegistryKeys.MODIFIER_PREDICATE_TYPE, helper -> {
            helper.register(id("config"), TectonicModifierPredicate.CODEC);
        });
    }

    private void registerCommands(RegisterCommandsEvent event) {
        TectonicCommand.register(event.getDispatcher());
    }

    private void addPack(String packName) {
        Path resourcePath = ModList.get().getModFileById("tectonic").getFile().findResource("resourcepacks/" + packName.toLowerCase());
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

    @Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBusEvents {
        @SubscribeEvent
        public static void onLoadComplete(FMLLoadCompleteEvent event) {
            ModContainer container = ModList.get().getModContainerById(Tectonic.MOD_ID).orElseThrow(() -> new IllegalStateException("Create mod container missing on LoadComplete"));
            container.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                    () -> new ConfigScreenHandler.ConfigScreenFactory((minecraft, parent) -> new ConfigScreen(parent))
            );
        }
    }
}