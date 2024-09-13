package io.github.rfmineguy.modulartools.be_renderers;

import io.github.rfmineguy.modulartools.blocks.infusion3.controller.InfusionControllerBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;

public class InfusionControllerBlockEntityRenderer implements BlockEntityRenderer<InfusionControllerBlockEntity> {
    public InfusionControllerBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }
    @Override
    public void render(InfusionControllerBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
    }
}
