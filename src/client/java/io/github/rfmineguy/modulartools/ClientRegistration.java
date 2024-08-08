package io.github.rfmineguy.modulartools;

import io.github.rfmineguy.modulartools.be_renderers.ModularInfusionControllerBlockEntityRenderer;
import io.github.rfmineguy.modulartools.be_renderers.ModularInfusionDroneBlock2EntityRenderer;
import io.github.rfmineguy.modulartools.be_renderers.ModularInfusionDroneBlockEntityRenderer;
import io.github.rfmineguy.modulartools.item_renderers.ActivatorItemRenderer;
import io.github.rfmineguy.modulartools.models.ActivatorModel;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class ClientRegistration {
    public static final EntityModelLayer ACTIVATOR_MODEL_LAYER = new EntityModelLayer(Identifier.of(ModularToolsMod.MODID, "activator"), "main");

    public static void registerAll() {
        BlockEntityRendererFactories.register(Registration.MODULAR_INFUSION_CONTROLLER_BLOCK_ENTITY, ModularInfusionControllerBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(Registration.MODULAR_INFUSION_DRONE_BLOCK_ENTITY, ModularInfusionDroneBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(Registration.MODULAR_INFUSION_DRONE2_BLOCK_ENTITY, ModularInfusionDroneBlock2EntityRenderer::new);

        // EntityRendererRegistry.register(Registration.ACTIVATOR_ENTITY_TYPE, ActivatorEntityRenderer::new);
        // EntityModelLayerRegistry.registerModelLayer(ACTIVATOR_MODEL_LAYER, ActivatorEntityModel::getTexturedModelData);

        BuiltinItemRendererRegistry.INSTANCE.register(Registration.LEVEL1_ACTIVATOR, new ActivatorItemRenderer());

        EntityModelLayerRegistry.registerModelLayer(ACTIVATOR_MODEL_LAYER, ActivatorModel::getTexturedModelData);
    }
}
