package io.github.rfmineguy.modulartools;

import io.github.rfmineguy.modulartools.be_renderers.*;
import io.github.rfmineguy.modulartools.blocks.infusion3.drone.InfusionDroneBlock;
import io.github.rfmineguy.modulartools.item_renderers.ActivatorItemRenderer;
import io.github.rfmineguy.modulartools.models.ActivatorModel;
import io.github.rfmineguy.modulartools.models.InfusionDroneModel;
import io.github.rfmineguy.modulartools.screen.ModularToolScreen;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.util.Identifier;

public class ClientRegistration {
    public static final EntityModelLayer ACTIVATOR_MODEL_LAYER = new EntityModelLayer(Identifier.of(ModularToolsMod.MODID, "activator"), "main");
    public static final EntityModelLayer INFUSION_DRONE_MODEL_LAYER = new EntityModelLayer(Identifier.of(ModularToolsMod.MODID, "models/block/infusion_drone"), "");


    public static void registerAll() {
        BlockEntityRendererFactories.register(ModRegistration.ModBlockEntities.MODULAR_INFUSION_CONTROLLER_BLOCK_ENTITY, ModularInfusionControllerBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModRegistration.ModBlockEntities.MODULAR_INFUSION_CONTROLLER2_BLOCK_ENTITY, ModularInfusionController2BlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModRegistration.ModBlockEntities.MODULAR_INFUSION_DRONE_BLOCK_ENTITY, ModularInfusionDroneBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModRegistration.ModBlockEntities.MODULAR_INFUSION_DRONE2_BLOCK_ENTITY, ModularInfusionDrone2BlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModRegistration.ModBlockEntities.INFUSION_CONTROLLER_BE, InfusionControllerBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModRegistration.ModBlockEntities.INFUSION_DRONE_BE, InfusionDroneBlockEntityRenderer::new);

        BuiltinItemRendererRegistry.INSTANCE.register(ModRegistration.ModItems.LEVEL1_ACTIVATOR, new ActivatorItemRenderer());

        EntityModelLayerRegistry.registerModelLayer(INFUSION_DRONE_MODEL_LAYER, InfusionDroneModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ACTIVATOR_MODEL_LAYER, ActivatorModel::getTexturedModelData);

        HandledScreens.register(ModRegistration.ModScreens.MODULAR_TOOL_SCREEN_HANDLER_EXTENDED_SCREEN_HANDLER, ModularToolScreen::new);
    }
}
