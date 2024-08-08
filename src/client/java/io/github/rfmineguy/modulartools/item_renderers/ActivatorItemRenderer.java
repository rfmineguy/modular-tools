package io.github.rfmineguy.modulartools.item_renderers;

import io.github.rfmineguy.modulartools.models.ActivatorModel;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;

public class ActivatorItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    private final ActivatorModel activatorModel = new ActivatorModel();

    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        // System.out.println("Render");
        VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getSolid());
        matrices.translate(0f, -1f, 0.5f);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotation(stack.getBobbingAnimationTime()));
        this.activatorModel.render(matrices, consumer, light, overlay);
        matrices.pop();
    }
}