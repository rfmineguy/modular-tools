package io.github.rfmineguy.modulartools.be_renderers;

import io.github.rfmineguy.modulartools.blocks.infusion2.modular_infusion_drone2.ModularInfusionDrone2BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class ModularInfusionDrone2BlockEntityRenderer implements BlockEntityRenderer<ModularInfusionDrone2BlockEntity> {
    public ModularInfusionDrone2BlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    // Code from ItemEntityRenderer
    @Override
    public void render(ModularInfusionDrone2BlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();

        // Render module item
        if (entity.hasModuleStack()) {
            BakedModel model = itemRenderer.getModel(entity.getModuleStack(), entity.getWorld(), (LivingEntity) null, 0);
            float j = MathHelper.sin((float) (entity.getAge() * 10 + entity.getAngleOffset())) * 0.1f + 0.75f;
            float k = model.getTransformation().getTransformation(ModelTransformationMode.GROUND).scale.y();
            matrices.push();
            matrices.translate(0.5F, j + 0.75F * k, 0.5F);
            matrices.scale(0.5f, 0.5f, 0.5f);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotation(entity.getAge() * 2));
            MinecraftClient.getInstance().getItemRenderer().renderItem(entity.getModuleStack(), ModelTransformationMode.GROUND, light, overlay, matrices, vertexConsumers, entity.getWorld(), 0);
            matrices.pop();
        }

        if (entity.hasLevelStack()) {
            matrices.push();
            matrices.translate(0.5f, 1/16f, 0.5f);
            matrices.scale(2f, 2f, 2f);
            MinecraftClient.getInstance().getItemRenderer().renderItem(entity.getModularLevel().getLevelStack(), ModelTransformationMode.GROUND, light, overlay, matrices, vertexConsumers, entity.getWorld(), 0);
            matrices.pop();
        }
    }
}
