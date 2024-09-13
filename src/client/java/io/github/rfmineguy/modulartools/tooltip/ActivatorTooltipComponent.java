package io.github.rfmineguy.modulartools.tooltip;

import io.github.rfmineguy.modulartools.components.ActivatorComponentRecord;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import org.joml.Matrix4f;

public class ActivatorTooltipComponent implements TooltipComponent {
    private final ActivatorComponentRecord activatorComponentRecord;

    public ActivatorTooltipComponent(ActivatorComponentRecord componentRecord) {
        this.activatorComponentRecord = componentRecord;
    }
    @Override
    public int getHeight() {
        return 10;
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        return textRenderer.getWidth(text());
    }

    @Override
    public void drawText(TextRenderer textRenderer, int x, int y, Matrix4f matrix, VertexConsumerProvider.Immediate vertexConsumers) {
        textRenderer.draw(text(), (float)x, (float)y, Colors.WHITE, false, matrix, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, 15728880);
    }

    private Text text() {
        return Text.literal("Level: " + activatorComponentRecord.getLevel());
    }
}
