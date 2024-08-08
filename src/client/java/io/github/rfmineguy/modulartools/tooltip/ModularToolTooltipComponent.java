package io.github.rfmineguy.modulartools.tooltip;

import io.github.rfmineguy.modulartools.components.ModularToolComponentRecord;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

public class ModularToolTooltipComponent implements TooltipComponent {
    private static final Identifier BACKGROUND_TEXTURE = Identifier.ofVanilla("container/bundle/background");
    private final ModularToolComponentRecord componentRecord;

    public ModularToolTooltipComponent(ModularToolComponentRecord componentRecord) {
        this.componentRecord = componentRecord;
    }
    @Override
    public int getHeight() {
        return 50;
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        return 50;
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
        TooltipComponent.super.drawItems(textRenderer, x, y, context);

        final int[] i = {0};
        final int[] j = {0};
        context.drawGuiTexture(BACKGROUND_TEXTURE, x, y, 50, 50);
        this.componentRecord.getModuleIds().forEach(identifier -> {
            if (Registries.ITEM.get(identifier) == Items.AIR) return;
            context.drawBorder(x + i[0] * 17, y + j[0] * 17, 16, 16, Colors.WHITE);
            Item item = Registries.ITEM.get(identifier);
            context.drawItem(item.getDefaultStack(), x + i[0] * 17, y + j[0] * 17);
            i[0]++;
            if (i[0] > 2) {
                i[0] = 0;
                j[0]++;
            }
        });
    }

    @Override
    public void drawText(TextRenderer textRenderer, int x, int y, Matrix4f matrix, VertexConsumerProvider.Immediate vertexConsumers) {
        TooltipComponent.super.drawText(textRenderer, x, y, matrix, vertexConsumers);
    }
}
