package io.github.rfmineguy.modulartools.be_renderers;

import io.github.rfmineguy.modulartools.blocks.infusion1.modular_infusion_drone.ModularInfusionDroneBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;

public class ModularInfusionDroneBlockEntityRenderer implements BlockEntityRenderer<ModularInfusionDroneBlockEntity> {
    public ModularInfusionDroneBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(ModularInfusionDroneBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        double y = Math.sin(entity.getYAngle());
        matrices.push();
        matrices.translate(0.5f, 2/16f + y, 0.5f);
        MinecraftClient.getInstance().getItemRenderer().renderItem(entity.getActiveStack(), ModelTransformationMode.GROUND, light, overlay, matrices, vertexConsumers, entity.getWorld(), 0);
        matrices.pop();
    }
}
