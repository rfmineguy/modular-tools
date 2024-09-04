package io.github.rfmineguy.modulartools;

import io.github.rfmineguy.modulartools.be_renderers.ModularInfusionController2BlockEntityRenderer;
import io.github.rfmineguy.modulartools.be_renderers.ModularInfusionControllerBlockEntityRenderer;
import io.github.rfmineguy.modulartools.be_renderers.ModularInfusionDrone2BlockEntityRenderer;
import io.github.rfmineguy.modulartools.be_renderers.ModularInfusionDroneBlockEntityRenderer;
import io.github.rfmineguy.modulartools.events.listeners.HudListener;
import io.github.rfmineguy.modulartools.events.listeners.ModularToolScrollListener;
import io.github.rfmineguy.modulartools.item_renderers.ActivatorItemRenderer;
import io.github.rfmineguy.modulartools.models.ActivatorModel;
import io.github.rfmineguy.modulartools.screen.ModularToolScreen;
import io.github.rfmineguy.modulartools.screen.ModularToolScreenHandler;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class ClientRegistration {
    public static final EntityModelLayer ACTIVATOR_MODEL_LAYER = new EntityModelLayer(Identifier.of(ModularToolsMod.MODID, "activator"), "main");

    public static void registerAll() {
        BlockEntityRendererFactories.register(ModRegistration.ModBlockEntities.MODULAR_INFUSION_CONTROLLER_BLOCK_ENTITY, ModularInfusionControllerBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModRegistration.ModBlockEntities.MODULAR_INFUSION_CONTROLLER2_BLOCK_ENTITY, ModularInfusionController2BlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModRegistration.ModBlockEntities.MODULAR_INFUSION_DRONE_BLOCK_ENTITY, ModularInfusionDroneBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModRegistration.ModBlockEntities.MODULAR_INFUSION_DRONE2_BLOCK_ENTITY, ModularInfusionDrone2BlockEntityRenderer::new);

        BuiltinItemRendererRegistry.INSTANCE.register(ModRegistration.ModItems.LEVEL1_ACTIVATOR, new ActivatorItemRenderer());

        EntityModelLayerRegistry.registerModelLayer(ACTIVATOR_MODEL_LAYER, ActivatorModel::getTexturedModelData);

        HandledScreens.register(ModRegistration.ModScreens.MODULAR_TOOL_SCREEN_HANDLER_EXTENDED_SCREEN_HANDLER, ModularToolScreen::new);

        // HudListener.register();
        // ModularToolScrollListener.register();
    }
}
