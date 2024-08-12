package io.github.rfmineguy.modulartools.be_renderers;

import io.github.rfmineguy.modulartools.ModularLevel;
import io.github.rfmineguy.modulartools.blocks.modular_infusion_controller_2.ModularInfusionController2BlockEntity;
import io.github.rfmineguy.modulartools.util.VecUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.util.Colors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

public class ModularInfusionController2BlockEntityRenderer implements BlockEntityRenderer<ModularInfusionController2BlockEntity> {
    public ModularInfusionController2BlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    private void renderConnectionTo(ModularInfusionController2BlockEntity controller, ModularInfusionController2BlockEntity.DroneData droneData) {
        Vec3d color = Vec3d.unpackRgb(switch (droneData.status) {
            case PRESENT -> Colors.WHITE;
            case MISSING -> Colors.YELLOW;
            case PRESENT_WITH_LEVEL_ITEM -> Colors.BLUE;
            case PRESENT_WITH_NEEDED_LEVEL_ITEM -> Colors.LIGHT_YELLOW;
            case NEVER_PLACED -> Colors.RED;
        });
        BlockPos pos = droneData.drone == null ? droneData.pos : droneData.drone.getPos();
        Vec3d particlePos = VecUtil.Lerp3d(pos.toCenterPos(), controller.getPos().toCenterPos(), controller.droneParticleLerp);
        controller.getWorld().addParticle(new DustParticleEffect(color.toVector3f(), 1.0f), particlePos.x, Math.floor(particlePos.y) + 0.02, particlePos.z, 0, 0, 0);
        // controller.getWorld().addParticle(new DustParticleEffect(color.subtract(40, 30, 0).toVector3f(), 1.3f), droneData.pos.getX() + 0.5, droneData.pos.getY() + 0.5, droneData.pos.getZ() + 0.5, 0, 0, 0);
    }

    @Override
    public void render(ModularInfusionController2BlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (entity.getWorld() == null) return;
        for (ModularInfusionController2BlockEntity.DroneData data : entity.getDrones()) {
            renderConnectionTo(entity, data);
        }
        if (entity.getToolStack() != ItemStack.EMPTY) {
            renderToolStack(entity, matrices, vertexConsumers, light, overlay);
        }
        if (entity.isMultiblockComplete() && entity.getModularLevel() != ModularLevel.NONE) {
           renderModularLevelStack(entity, matrices, vertexConsumers, light, overlay);
        }
    }

    private void renderToolStack(ModularInfusionController2BlockEntity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();;
        BakedModel model = itemRenderer.getModel(entity.getToolStack(), entity.getWorld(), (LivingEntity) null, 0);
        float j = MathHelper.sin((float) (entity.getAge() * 10)) * 0.1f + 1.2f;
        float k = model.getTransformation().getTransformation(ModelTransformationMode.GROUND).scale.y();
        matrices.push();
        matrices.translate(0.5f, j + 0.3f * k, 0.5f);
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotation(entity.getToolStackRotationData().toolStackRotation - (float)Math.PI/2.f));
        MinecraftClient.getInstance().getItemRenderer().renderItem(entity.getToolStack(), ModelTransformationMode.GROUND, light, overlay, matrices, vertexConsumers, entity.getWorld(), 0);
        matrices.pop();
    }

    private void renderModularLevelStack(ModularInfusionController2BlockEntity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();;
        BakedModel model = itemRenderer.getModel(entity.getModularLevelStack(), entity.getWorld(), (LivingEntity) null, 0);
        matrices.push();
        float j = MathHelper.sin(entity.getAge() * 10 + MathHelper.HALF_PI) * 0.1f;
        float k = model.getTransformation().getTransformation(ModelTransformationMode.GROUND).scale.y();
        matrices.translate(0.5, 0.2 + j + 0.7 * k, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotation(entity.getAge()));
        // matrices.multiply(RotationAxis.POSITIVE_Z.rotation(entity.getAge()));
        MinecraftClient.getInstance().getItemRenderer().renderItem(entity.getModularLevel().getLevelStack(), ModelTransformationMode.GROUND, light, overlay, matrices, vertexConsumers, entity.getWorld(), 0);
        matrices.pop();
    }
}