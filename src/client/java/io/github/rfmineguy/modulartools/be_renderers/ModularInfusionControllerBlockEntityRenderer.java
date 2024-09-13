package io.github.rfmineguy.modulartools.be_renderers;

import io.github.rfmineguy.modulartools.blocks.infusion1.modular_infusion_controller.ModularInfusionControllerBlockEntity;
import io.github.rfmineguy.modulartools.util.VecUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class ModularInfusionControllerBlockEntityRenderer implements BlockEntityRenderer<ModularInfusionControllerBlockEntity> {
    public ModularInfusionControllerBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    private void renderConnectionBetween(BlockPos from, BlockPos to, World world) {
        // System.out.println("Render from: " + from + "; to: " + to);
        final int RESOLUTION = 20;
        for (int i = 0; i < RESOLUTION; i++) {
            Vec3d particlePos = VecUtil.Lerp3d(from.toCenterPos(), to.toCenterPos(), (float) i / (float) RESOLUTION);
            world.addParticle(ParticleTypes.HAPPY_VILLAGER, particlePos.x, Math.floor(particlePos.y) + 0.1, particlePos.z, 0, 0, 0);
        }
    }

    @Override
    public void render(ModularInfusionControllerBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        List<BlockPos> drones = entity.getDroneAsList();
        if (entity.getWorld().getTime() % 50 == 0) {
            for (BlockPos pos: drones) {
                renderConnectionBetween(entity.getPos(), pos, entity.getWorld());
            }
        }
        matrices.push();
        matrices.translate(0.5f, 0.7f, 0.5f);
        MinecraftClient.getInstance().getItemRenderer().renderItem(entity.getActiveStack(), ModelTransformationMode.GROUND, light, overlay, matrices, vertexConsumers, entity.getWorld(), 0);
        matrices.pop();
    }
}
