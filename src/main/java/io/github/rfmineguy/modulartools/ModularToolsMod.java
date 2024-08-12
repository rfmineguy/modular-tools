package io.github.rfmineguy.modulartools;

import io.github.rfmineguy.modulartools.util.CommonInputUtil;
import io.github.rfmineguy.modulartools.util.InputUtil;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModularToolsMod implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String MODID = "modulartools";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
	public static InputUtil INPUT_UTIL;

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");
		ModRegistration.registerAll();
		INPUT_UTIL = new CommonInputUtil();
	}
}