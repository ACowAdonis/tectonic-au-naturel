package dev.worldgen.tectonic.client.gui.widget;

import net.minecraft.client.InputType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

import java.util.function.Consumer;

public class SliderWidget extends AbstractWidget {
    private static final ResourceLocation SLIDER_SPRITE = ResourceLocation.withDefaultNamespace("widget/slider");
    private static final ResourceLocation HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("widget/slider_highlighted");
    private static final ResourceLocation SLIDER_HANDLE_SPRITE = ResourceLocation.withDefaultNamespace("widget/slider_handle");
    private static final ResourceLocation SLIDER_HANDLE_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("widget/slider_handle_highlighted");
    private final double min;
    private final double max;
    private final double step;
    private final String name;
    private final Consumer<Double> action;
    protected double delta;
    protected double value;
    private boolean canChangeValue;
    protected boolean displayInt;

    public SliderWidget(double min, double max, double step, String suffix, Consumer<Double> action, double value, boolean displayInt, double base) {
        super(0, 0, 0, 0, CommonComponents.EMPTY);
        this.min = min;
        this.max = max;
        this.step = 1 / step;
        this.name = "config.tectonic." + suffix;
        this.action = action;
        this.value = value;
        this.delta = valueToDelta();
        this.displayInt = displayInt;

        MutableComponent text = Component.empty();
        text.append(Component.translatable(this.name + ".tooltip"));
        text.append(CommonComponents.NEW_LINE);
        text.append(Component.translatable("config.tectonic.default"));
        if (this.displayInt) {
            text.append("§e" + (int) base);
        } else {
            text.append("§e" + base);
        }
        this.setTooltip(Tooltip.create(text));
        this.updateMessage();
    }

    private ResourceLocation getSprite() {
        return this.isFocused() && !this.canChangeValue ? HIGHLIGHTED_SPRITE : SLIDER_SPRITE;
    }

    private ResourceLocation getHandleSprite() {
        return !this.isHovered && !this.canChangeValue ? SLIDER_HANDLE_SPRITE : SLIDER_HANDLE_HIGHLIGHTED_SPRITE;
    }

    protected MutableComponent createNarrationMessage() {
        return Component.translatable("gui.narrate.slider", this.getMessage());
    }

    public void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE, this.createNarrationMessage());
        if (this.active) {
            if (this.isFocused()) {
                narrationElementOutput.add(NarratedElementType.USAGE, Component.translatable("narration.slider.usage.focused"));
            } else {
                narrationElementOutput.add(NarratedElementType.USAGE, Component.translatable("narration.slider.usage.hovered"));
            }
        }

    }

    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, this.getSprite(), this.getX(), this.getY(), this.getWidth(), this.getHeight(), ARGB.white(this.alpha));
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, this.getHandleSprite(), this.getX() + (int)(this.delta * (double)(this.width - 8)), this.getY(), 8, this.getHeight(), ARGB.white(this.alpha));
        int k = this.active ? 16777215 : 10526880;
        this.renderScrollingString(guiGraphics, minecraft.font, 2, k | Mth.ceil(this.alpha * 255.0F) << 24);
    }

    public void onClick(MouseButtonEvent event, boolean bl) {
        this.setValueFromMouse(event.x());
    }

    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (!focused) {
            this.canChangeValue = false;
        } else {
            InputType inputtype = Minecraft.getInstance().getLastInputType();
            if (inputtype == InputType.MOUSE || inputtype == InputType.KEYBOARD_TAB) {
                this.canChangeValue = true;
            }
        }

    }

    public boolean keyPressed(KeyEvent event) {
        if (event.isSelection()) {
            this.canChangeValue = !this.canChangeValue;
            return true;
        } else {
            if (this.canChangeValue) {
                boolean flag = event.key() == 263;
                if (flag || event.key() == 262) {
                    float f = flag ? -1.0F : 1.0F;
                    this.setValue(this.delta + (double)(f / (float)(this.width - 8)));
                    return true;
                }
            }

            return false;
        }
    }

    private void setValueFromMouse(double mouseX) {
        this.setValue((mouseX - (double)(this.getX() + 4)) / (double)(this.width - 8));
    }

    private void setValue(double value) {
        double d0 = this.delta;
        this.delta = Mth.clamp(value, 0.0, 1.0);
        this.value = deltaToValue();

        if (d0 != this.delta) {
            this.action.accept(this.value);
        }

        this.updateMessage();
    }

    private double deltaToValue() {
        double lerped = Mth.lerp(this.delta, this.min, this.max);
        return Math.round(lerped * this.step) / this.step;
    }

    private double valueToDelta() {
        return (this.value - this.min) / (this.max - this.min);
    }

    protected void onDrag(MouseButtonEvent event, double dragX, double dragY) {
        this.setValueFromMouse(event.x());
        super.onDrag(event, dragX, dragY);
    }

    public void playDownSound(SoundManager handler) {
    }

    public void onRelease(MouseButtonEvent event) {
        super.playDownSound(Minecraft.getInstance().getSoundManager());
    }

    private void updateMessage() {
        if (this.displayInt) {
            this.setMessage(Component.translatable(this.name, (int) this.value));
        } else {
            this.setMessage(Component.translatable(this.name, this.value));
        }
    }
}