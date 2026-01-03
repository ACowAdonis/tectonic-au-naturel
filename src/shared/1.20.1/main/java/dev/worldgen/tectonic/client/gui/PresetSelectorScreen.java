package dev.worldgen.tectonic.client.gui;

import dev.worldgen.tectonic.config.ConfigHandler;
import dev.worldgen.tectonic.config.state.ConfigPresets;
import dev.worldgen.tectonic.config.state.ConfigState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.List;

public class PresetSelectorScreen extends Screen {
    private final ConfigScreen parent;

    private PresetList list;

    public PresetSelectorScreen(ConfigScreen parent) {
        super(text("title"));
        this.parent = parent;
    }

    @Override
    public void init() {
        list = new PresetList(minecraft, width, this);
        ConfigPresets.acceptPresets(list::addEntry);

        this.addWidget(list);

        this.addRenderableWidget(Button.builder(
            CommonComponents.GUI_CANCEL,
            button -> this.onClose()
        ).pos(width / 2 - 100, height - 28).size(200, 20).build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        list.render(guiGraphics, mouseX, mouseY, delta);
        super.render(guiGraphics, mouseX, mouseY, delta);

        guiGraphics.drawCenteredString(this.font, title, width / 2, 12, 0xffffff);
    }

    public void onClose() {
        this.minecraft.setScreen(new ConfigScreen(this.parent.parent));
    }

    public static Component text(String name) {
        return Component.translatable("preset.tectonic." + name);
    }

    public class PresetList extends ContainerObjectSelectionList<PresetList.Entry> {
        public PresetList(Minecraft minecraft, int width, PresetSelectorScreen parent) {
            super(minecraft, width, parent.height, 32, parent.height - 32, 25);
        }

        public void addEntry(String name, ConfigState state, int color) {
            Entry entry = new Entry(name, state, color);
            entry.widget.setX(width / 2 - 155);
            entry.widget.setY(0);
            entry.widget.setWidth(310);
            this.addEntry(entry);
        }

        public int getRowWidth() {
            return 310;
        }

        public class Entry extends ContainerObjectSelectionList.Entry<Entry> {
            final Button widget;
            final ConfigState state;

            Entry(String name, ConfigState state, int color) {
                this.widget = Button.builder(text(name).copy().withStyle(s -> s.withColor(color)), this::select).width(310).build();
                this.state = state;
            }

            private void select(Button button) {
                ConfigHandler.setState(state);
                ConfigHandler.save();
                PresetSelectorScreen.this.onClose();
            }

            public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
                this.widget.setY(top);
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