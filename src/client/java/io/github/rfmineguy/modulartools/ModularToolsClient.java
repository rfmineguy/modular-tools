package io.github.rfmineguy.modulartools;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ModularToolsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientRegistration.registerAll();
		// BlockEntity renderers

		// register(EntityType.TRIDENT, TridentEntityRenderer::new);

		ModularToolsMod.LOGGER.info("Client initialized");
		ModularToolsMod.INPUT_UTIL = new ClientInputUtil();
	}
}