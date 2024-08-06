package io.github.rfmineguy.modulartools.be_renderers;

import io.github.rfmineguy.modulartools.blocks.modular_infusion_controller.ModularInfusionControllerBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;

public class ModularInfusionControllerBlockEntityRenderer implements BlockEntityRenderer<ModularInfusionControllerBlockEntity> {
    public ModularInfusionControllerBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(ModularInfusionControllerBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        System.out.println("render");
        matrices.push();
        matrices.translate(0.5f, 0.7f, 0.5f);
        MinecraftClient.getInstance().getItemRenderer().renderItem(entity.getActiveStack(), ModelTransformationMode.GROUND, light, overlay, matrices, vertexConsumers, entity.getWorld(), 0);
        matrices.pop();
    }
}
