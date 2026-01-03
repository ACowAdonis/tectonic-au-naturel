package dev.worldgen.tectonic;

import dev.worldgen.tectonic.client.gui.ConfigScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = Tectonic.MOD_ID, dist = Dist.CLIENT)
public class TectonicNeoforgeClient {
    public TectonicNeoforgeClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, (modContainer, parent) -> new ConfigScreen(parent));
    }
}