package io.github.rfmineguy.modulartools.be_renderers;

import io.github.rfmineguy.modulartools.ModRegistration;
import io.github.rfmineguy.modulartools.blocks.infusion3.drone.InfusionDroneBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

public class InfusionDroneBlockEntityRenderer implements BlockEntityRenderer<InfusionDroneBlockEntity> {
    private final BlockRenderManager blockRenderManager;
    private final ItemRenderer itemRenderer;

    public InfusionDroneBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
        itemRenderer = MinecraftClient.getInstance().getItemRenderer();
    }
    @Override
    public void render(InfusionDroneBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        assert entity.getWorld() != null;
        VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getSolid());
        Vec3d subPos = entity.getSubPosition();
        matrices.push();
        matrices.translate(subPos.x, subPos.y, subPos.z);
        blockRenderManager.renderBlock(ModRegistration.ModBlocks.INFUSION_DRONE.getDefaultState(), entity.getPos(), entity.getWorld(), matrices, consumer, true, entity.getWorld().random);
        matrices.pop();

        ItemStack s = entity.getActiveLevelStack();
        if (s != null) {
            matrices.push();
            matrices.translate(subPos.x + 0.5, subPos.y, subPos.z + 0.5);
            matrices.scale(1.7f, 0.1f, 1.7f);
            itemRenderer.renderItem(s, ModelTransformationMode.GROUND, light, overlay, matrices, vertexConsumers, entity.getWorld(), 0);
            matrices.pop();
        }

        s = entity.getActiveModuleStack();
        if (s != null) {
            matrices.push();
            matrices.translate(subPos.x + 0.5, subPos.y + 0.2, subPos.z + 0.5);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotation(entity.getModuleRotation()));
            itemRenderer.renderItem(s, ModelTransformationMode.GROUND, light, overlay, matrices, vertexConsumers, entity.getWorld(), 0);
            matrices.pop();
        }

        s = entity.getActivatorStack();
        if (s != null) {
            matrices.push();
            matrices.translate(subPos.x + 0.5, subPos.y + 0.2, subPos.z + 0.5);
            matrices.multiply(RotationAxis.NEGATIVE_Y.rotation(entity.getModuleRotation() - (float)Math.PI/2.f));
            itemRenderer.renderItem(s, ModelTransformationMode.GROUND, light, overlay, matrices, vertexConsumers, entity.getWorld(), 0);
            matrices.pop();
        }
    }
}
