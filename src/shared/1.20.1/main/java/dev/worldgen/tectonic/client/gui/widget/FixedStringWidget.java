package dev.worldgen.tectonic.client.gui.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;

public class FixedStringWidget extends StringWidget {
    public FixedStringWidget(Component component, Font font) {
        super(component, font);
    }

    public void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
        Component component = this.getMessage();
        Font font = this.getFont();
        int width = this.getWidth();
        int textWidth = font.width(component);
        int m = this.getX() + Math.round(0.5f * (float)(width - textWidth));
        int n = this.getY() + 10 + (this.getHeight() - 9) / 2;
        FormattedCharSequence formattedCharSequence = textWidth > width ? this.clipText(component, width) : component.getVisualOrderText();
        guiGraphics.drawString(font, formattedCharSequence, m, n, this.getColor());
    }

    private FormattedCharSequence clipText(Component component, int i) {
        Font font = this.getFont();
        FormattedText formattedText = font.substrByWidth(component, i - font.width((CommonComponents.ELLIPSIS)));
        return Language.getInstance().getVisualOrder(FormattedText.composite(formattedText, CommonComponents.ELLIPSIS));
    }
}