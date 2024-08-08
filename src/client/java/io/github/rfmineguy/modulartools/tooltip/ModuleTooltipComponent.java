package io.github.rfmineguy.modulartools.tooltip;

import io.github.rfmineguy.modulartools.components.ModuleComponentRecord;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import org.joml.Matrix4f;

public class ModuleTooltipComponent implements TooltipComponent {
    private final ModuleComponentRecord component;
    public ModuleTooltipComponent(ModuleComponentRecord component) {
        this.component = component;
    }

    @Override
    public int getHeight() {
        return 10;
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        return textRenderer.getWidth("Pedestal Requirement: " + component.getLevel());
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
        TooltipComponent.super.drawItems(textRenderer, x, y, context);
        context.drawText(textRenderer, Text.literal("Pedestal Requirement: " + Formatting.UNDERLINE + component.getLevel()), x, y, Colors.WHITE, false);
    }

    @Override
    public void drawText(TextRenderer textRenderer, int x, int y, Matrix4f matrix, VertexConsumerProvider.Immediate vertexConsumers) {
        TooltipComponent.super.drawText(textRenderer, x, y, matrix, vertexConsumers);
    }
}
