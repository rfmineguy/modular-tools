package io.github.rfmineguy.modulartools;

import io.github.rfmineguy.modulartools.be_renderers.ModularInfusionDroneBlockEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

@Environment(EnvType.CLIENT)
public class ModularToolsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BlockEntityRendererFactories.register(Registration.MODULAR_INFUSION_DRONE_BLOCK_ENTITY_BLOCK, ModularInfusionDroneBlockEntityRenderer::new);
		System.out.println("Client initialized");
	}
}