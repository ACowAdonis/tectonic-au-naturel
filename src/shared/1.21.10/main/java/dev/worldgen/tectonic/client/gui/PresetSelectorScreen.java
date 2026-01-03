package dev.worldgen.tectonic.client.gui;

import dev.worldgen.tectonic.config.ConfigHandler;
import dev.worldgen.tectonic.config.state.ConfigPresets;
import dev.worldgen.tectonic.config.state.ConfigState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.List;

public class PresetSelectorScreen extends Screen {
    private final ConfigScreen parent;

    private PresetList list;
    final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);

    public PresetSelectorScreen(ConfigScreen parent) {
        super(text("title"));
        this.parent = parent;
    }

    @Override
    public void init() {
        layout.addTitleHeader(title, font);

        list = layout.addToContents(new PresetList(minecraft, width, this));
        ConfigPresets.acceptPresets(list::addEntry);

        LinearLayout footer = layout.addToFooter(LinearLayout.horizontal().spacing(8));
        footer.addChild(Button.builder(CommonComponents.GUI_CANCEL, button -> onClose()).build());

        layout.visitWidgets(this::addRenderableWidget);
        this.repositionElements();
    }

    protected void repositionElements() {
        this.layout.arrangeElements();
        if (this.list != null) {
            this.list.updateSize(this.width, this.layout);
        }
    }

    public void onClose() {
        this.minecraft.setScreen(new ConfigScreen(this.parent.parent));
    }

    public static Component text(String name) {
        return Component.translatable("preset.tectonic." + name);
    }

    public class PresetList extends ContainerObjectSelectionList<PresetList.Entry> {
        public PresetList(Minecraft minecraft, int width, PresetSelectorScreen parent) {
            super(minecraft, width, parent.layout.getContentHeight(), parent.layout.getHeaderHeight(), 25);
        }

        public void addEntry(String name, ConfigState state, int color) {
            this.addEntry(new Entry(name, state, color));
        }

        public int getRowWidth() {
            return 310;
        }

        public void updateSize(int width, HeaderAndFooterLayout layout) {
            super.updateSize(width, layout);
            this.children().forEach(entry -> entry.widget.setX(width / 2 - 155));
        }

        public class Entry extends ContainerObjectSelectionList.Entry<Entry> {
            final Button widget;
            final ConfigState state;

            Entry(String name, ConfigState state, int color) {
                this.widget = Button.builder(text(name).copy().withColor(color), this::select).width(310).build();
                this.state = state;
            }

            private void select(Button button) {
                ConfigHandler.setState(state);
                ConfigHandler.save();
                PresetSelectorScreen.this.onClose();
            }

            @Override
            public void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean hovering, float partialTick) {
                this.widget.setY(this.getY());
                this.widget.render(guiGraphics, mouseX, mouseY, partialTick);
            }

            public List<? extends GuiEventListener> children() {
                return List.of(this.widget);
            }

            public List<? extends NarratableEntry> narratables() {
                return List.of(this.widget);
            }
        }
    }

}