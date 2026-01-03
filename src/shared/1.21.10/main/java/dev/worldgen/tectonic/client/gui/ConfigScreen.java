package dev.worldgen.tectonic.client.gui;

import dev.worldgen.tectonic.config.ConfigHandler;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ConfigScreen extends Screen {
    protected final Screen parent;

    private ConfigList list;
    final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);

    public ConfigScreen(Screen parent) {
        super(text("title"));
        this.parent = parent;
    }

    @Override
    public void init() {
        layout.addTitleHeader(title, font);

        list = layout.addToContents(new ConfigList(minecraft, width, this));
        list.addEntry(Button.builder(ConfigScreen.text("view_presets"), button -> minecraft.setScreen(new PresetSelectorScreen(this))).build());
        list.build(font);


        LinearLayout footer = layout.addToFooter(LinearLayout.horizontal().spacing(8));

        footer.addChild(Button.builder(CommonComponents.GUI_DONE, button -> onDone()).build());

        layout.visitWidgets(this::addRenderableWidget);
        this.repositionElements();
    }

    protected void repositionElements() {
        this.layout.arrangeElements();
        if (this.list != null) {
            this.list.updateSize(this.width, this.layout);
        }
    }

    private void onDone() {
        ConfigHandler.save();
        this.onClose();
    }

    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }

    public static Component text(String name) {
        return Component.translatable("config.tectonic." + name);
    }

    public static Component option(String name) {
        return text("option." + name);
    }
}