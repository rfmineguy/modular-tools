package io.github.rfmineguy.modulartools;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import io.github.rfmineguy.modulartools.blocks.modular_infusion_controller.ModularInfusionControllerBlock;
import io.github.rfmineguy.modulartools.blocks.modular_infusion_controller.ModularInfusionControllerBlockEntity;
import io.github.rfmineguy.modulartools.blocks.modular_infusion_controller_2.ModularInfusionController2Block;
import io.github.rfmineguy.modulartools.blocks.modular_infusion_controller_2.ModularInfusionController2BlockEntity;
import io.github.rfmineguy.modulartools.blocks.modular_infusion_drone.ModularInfusionDroneBlockEntity;
import io.github.rfmineguy.modulartools.blocks.modular_infusion_drone.ModularInfusionDroneBlock;
import io.github.rfmineguy.modulartools.blocks.modular_infusion_drone2.ModularInfusionDrone2Block;
import io.github.rfmineguy.modulartools.blocks.modular_infusion_drone2.ModularInfusionDrone2BlockEntity;
import io.github.rfmineguy.modulartools.commands.ModuleCommand;
import io.github.rfmineguy.modulartools.commands.RepairCommand;
import io.github.rfmineguy.modulartools.components.LevelBlockComponentRecord;
import io.github.rfmineguy.modulartools.components.ModularToolComponentRecord;
import io.github.rfmineguy.modulartools.components.ModularToolComponentRecord2;
import io.github.rfmineguy.modulartools.components.ModuleComponentRecord;
import io.github.rfmineguy.modulartools.events.listeners.BlockBreakDirectionListener;
import io.github.rfmineguy.modulartools.items.ActivatorItem;
import io.github.rfmineguy.modulartools.items.LevelBlockItem;
import io.github.rfmineguy.modulartools.items.modulartools.*;
import io.github.rfmineguy.modulartools.items.ModuleItem;
import io.github.rfmineguy.modulartools.modules.Module;
import io.github.rfmineguy.modulartools.networking.ModularToolSyncPayload;
import io.github.rfmineguy.modulartools.screen.ModularToolScreenHandler;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.registry.*;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.UUID;
import java.util.function.Supplier;

public class ModRegistration {
    public static class ModRegistries {
        public static final RegistryKey<Registry<Module>> MODULE_REG_KEY = RegistryKey.ofRegistry(Identifier.of(ModularToolsMod.MODID, "modules"));
        public static final Registry<Module> MODULE_REGISTRY = FabricRegistryBuilder.createSimple(MODULE_REG_KEY)
                .attribute(RegistryAttribute.SYNCED)
                .buildAndRegister();

        public static void registerAll() {}
    }
    public static class ModItems {
        private static Item registerItem(String idString, Item item) {
            Identifier id  = Identifier.of(ModularToolsMod.MODID, idString);
            return Registry.register(Registries.ITEM, id, item);
        }
        private static ActivatorItem registerActivatorItem(String idString, ModularLevel level) {
            Identifier id = Identifier.of(ModularToolsMod.MODID, idString);
            return Registry.register(Registries.ITEM, id, new ActivatorItem(new Item.Settings()));
        }

        public static final Item MODULARIUM                   = registerItem("modularium", new Item(new Item.Settings()));
        public static final Item MODULAR_PICKAXE              = registerItem("modular_pickaxe", new ModularPickaxeItem(ToolMaterials.IRON, new Item.Settings().component(ModComponents.MODULAR_TOOL_COMPONENT, ModularToolComponentRecord.DEFAULT).customDamage(new ModularToolDamageHandler())));
        public static final Item MODULAR_SHOVEL               = registerItem("modular_shovel", new ModularShovelItem(ToolMaterials.IRON, new Item.Settings().component(ModComponents.MODULAR_TOOL_COMPONENT, ModularToolComponentRecord.DEFAULT).customDamage(new ModularToolDamageHandler())));
        public static final Item MODULAR_AXE                  = registerItem("modular_axe", new ModularAxeItem(ToolMaterials.IRON, new Item.Settings().component(ModComponents.MODULAR_TOOL_COMPONENT, ModularToolComponentRecord.DEFAULT).customDamage(new ModularToolDamageHandler())));
        public static final Item MODULAR_HOE                  = registerItem("modular_hoe", new ModularHoeItem(ToolMaterials.IRON, new Item.Settings().component(ModComponents.MODULAR_TOOL_COMPONENT, ModularToolComponentRecord.DEFAULT).customDamage(new ModularToolDamageHandler())));
        // public static final Item MODULAR_SWORD                = registerItem("modular_sword", new ModularShovelItem(ToolMaterials.IRON, new Item.Settings().component(ModComponents.MODULAR_TOOL_COMPONENT, ModularToolComponentRecord.DEFAULT).customDamage(new ModularToolDamageHandler())));
        public static final ActivatorItem LEVEL1_ACTIVATOR    = registerActivatorItem("level1_activator", ModularLevel.ONE);

        public static void registerAll() {}
    }
    public static class ModBlocks {
        private static Block registerBlock(String idString, Block block) {
            Identifier id = Identifier.of(ModularToolsMod.MODID, idString);
            Block b = Registry.register(Registries.BLOCK, id, block);
            ModItems.registerItem(idString, new BlockItem(b, new Item.Settings()));
            return b;
        }
        private static Block registerLevelBlock(String idString, ModularLevel level, Block block) {
            Identifier id = Identifier.of(ModularToolsMod.MODID, idString);
            Block b = Registry.register(Registries.BLOCK, id, block);
            ModItems.registerItem(idString, new LevelBlockItem(b, new Item.Settings().component(ModRegistration.ModComponents.LEVEL_BLOCK_COMPONENT, LevelBlockComponentRecord.ofLevel(level))));
            return b;
        }

        public static final Block MODULARIUM_ORE              = registerBlock("modularium_ore", new Block(AbstractBlock.Settings.copy(Blocks.DIAMOND_ORE)));
        public static final Block DEEPSLATE_MODULARIUM_ORE    = registerBlock("deepslate_modularium_ore", new Block(AbstractBlock.Settings.copy(Blocks.DIAMOND_ORE)));
        public static final Block MODULAR_INFUSION_CONTROLLER = registerBlock("modular_infusion_controller", new ModularInfusionControllerBlock());
        public static final Block MODULAR_INFUSION_CONTROLLER2 = registerBlock("modular_infusion_controller2", new ModularInfusionController2Block());
        public static final Block MODULAR_INFUSION_DRONE      = registerBlock("modular_infusion_drone", new ModularInfusionDroneBlock());
        public static final Block MODULAR_INFUSION_DRONE2     = registerBlock("modular_infusion_drone2", new ModularInfusionDrone2Block());

        public static final Block TEST_CONTROLLER             = registerBlock("controller_test", new Block(AbstractBlock.Settings.copy(Blocks.ANVIL)));

        // Activators
        public static final Block ERROR_BLOCK                 = registerBlock("error", new Block(AbstractBlock.Settings.create()));
        public static final Block LEVEL1_BLOCK                = registerLevelBlock("level_1", ModularLevel.ONE, new Block(AbstractBlock.Settings.copy(Blocks.ANVIL)));
        public static final Block LEVEL2_BLOCK                = registerLevelBlock("level_2", ModularLevel.TWO, new Block(AbstractBlock.Settings.copy(Blocks.ANVIL)));
        public static final Block LEVEL3_BLOCK                = registerLevelBlock("level_3", ModularLevel.THREE, new Block(AbstractBlock.Settings.copy(Blocks.ANVIL)));
        public static final Block LEVEL4_BLOCK                = registerLevelBlock("level_4", ModularLevel.FOUR, new Block(AbstractBlock.Settings.copy(Blocks.ANVIL)));
        public static final Block LEVEL5_BLOCK                = registerLevelBlock("level_5", ModularLevel.FIVE, new Block(AbstractBlock.Settings.copy(Blocks.ANVIL)));
        public static final Block LEVEL6_BLOCK                = registerLevelBlock("level_6", ModularLevel.SIX, new Block(AbstractBlock.Settings.copy(Blocks.ANVIL)));
        public static final Block LEVEL7_BLOCK                = registerLevelBlock("level_7", ModularLevel.SEVEN, new Block(AbstractBlock.Settings.copy(Blocks.ANVIL)));
        public static final Block LEVEL8_BLOCK                = registerLevelBlock("level_8", ModularLevel.EIGHT, new Block(AbstractBlock.Settings.copy(Blocks.ANVIL)));

        public static void registerAll() {}
    }
    public static class ModModules {

        private static Module registerModuleAndItem(String idString, Module module) {
            ModItems.registerItem(idString, new ModuleItem(new Item.Settings().component(ModComponents.MODULE_COMPONENT, ModuleComponentRecord.ofLevel(module.getLevelEnum()))));
            return Registry.register(ModRegistries.MODULE_REGISTRY,
                    Identifier.of(ModularToolsMod.MODID, idString),
                    module.setId(Identifier.of(ModularToolsMod.MODID, idString)));
        }
        public static final Module MINING_SPEED_ONE           = registerModuleAndItem("speed.one", Module.of(ModularLevel.ONE, ModuleCategory.SPEED));
        public static final Module MINING_SPEED_TWO           = registerModuleAndItem("speed.two", Module.of(ModularLevel.TWO, ModuleCategory.SPEED));
        public static final Module MINING_SIZE_ONE            = registerModuleAndItem("3x3.one", Module.of(ModularLevel.ONE, ModuleCategory.MINING_SIZE));
        public static final Module MINING_SIZE_TWO            = registerModuleAndItem("5x5.one", Module.of(ModularLevel.TWO, ModuleCategory.MINING_SIZE));
        public static final Module MINING_SIZE_THREE          = registerModuleAndItem("21x21.one", Module.of(ModularLevel.THREE, ModuleCategory.MINING_SIZE));
        public static final Module UNBREAKABLE                = registerModuleAndItem("unbreakable.one", Module.of(ModularLevel.THREE, ModuleCategory.UNBREAKABLE)) ;

        public static void registerAll() {}
    }
    public static class ModComponents {
        private static <T> ComponentType<T> registerComponent(String id, Supplier<ComponentType.Builder<T>> componentTypeBuilder) {
            return Registry.register(Registries.DATA_COMPONENT_TYPE, id, componentTypeBuilder.get().build());
        }

        public static final ComponentType<ModularToolComponentRecord> MODULAR_TOOL_COMPONENT = registerComponent("modular_tool", () -> {
            return ComponentType.<ModularToolComponentRecord>builder().codec(ModularToolComponentRecord.CODEC);
        });
        public static final ComponentType<LevelBlockComponentRecord> LEVEL_BLOCK_COMPONENT = registerComponent("level_block", () -> {
            return ComponentType.<LevelBlockComponentRecord>builder().codec(LevelBlockComponentRecord.CODEC);
        });
        public static final ComponentType<ModuleComponentRecord> MODULE_COMPONENT = registerComponent("module", () -> {
            return ComponentType.<ModuleComponentRecord>builder().codec(ModuleComponentRecord.CODEC);
        });

        public static void registerAll() {}
    }
    public static class ModBlockEntities {
        private static <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(String idString, Block block, BlockEntityType.BlockEntityFactory<T> factory) {
            return Registry.register(
                    Registries.BLOCK_ENTITY_TYPE,
                    Identifier.of(ModularToolsMod.MODID, idString),
                    BlockEntityType.Builder.create(factory, block).build(null)
            );
        }

        public static final BlockEntityType<ModularInfusionControllerBlockEntity> MODULAR_INFUSION_CONTROLLER_BLOCK_ENTITY
                = registerBlockEntity("modular_infusion_controller_block_entity",
                ModBlocks.MODULAR_INFUSION_CONTROLLER,
                ModularInfusionControllerBlockEntity::new);
        public static final BlockEntityType<ModularInfusionController2BlockEntity> MODULAR_INFUSION_CONTROLLER2_BLOCK_ENTITY
                = registerBlockEntity("modular_infusion_controller2_block_entity",
                ModBlocks.MODULAR_INFUSION_CONTROLLER2,
                ModularInfusionController2BlockEntity::new);
        public static final BlockEntityType<ModularInfusionDroneBlockEntity> MODULAR_INFUSION_DRONE_BLOCK_ENTITY
                = registerBlockEntity("modular_infusion_drone_block_entity",
                ModBlocks.MODULAR_INFUSION_DRONE,
                ModularInfusionDroneBlockEntity::new);
        public static final BlockEntityType<ModularInfusionDrone2BlockEntity> MODULAR_INFUSION_DRONE2_BLOCK_ENTITY
                = registerBlockEntity("modular_infusion_drone2_block_entity",
                ModBlocks.MODULAR_INFUSION_DRONE2,
                ModularInfusionDrone2BlockEntity::new);

        public static void registerAll() {}
    }
    public static class ModItemGroups {
        private static ItemGroup registerItemGroup(String idString, ItemGroup itemGroup) {
            return Registry.register(Registries.ITEM_GROUP, Identifier.of(ModularToolsMod.MODID, idString), itemGroup);
        }

        public static final ItemGroup MODULAR_TOOLS_ITEM_GROUP = registerItemGroup("main", FabricItemGroup.builder()
                .icon(() -> new ItemStack(ModItems.MODULARIUM))
                .displayName(Text.translatable("itemGroup.modular_tools.main"))
                .entries(((displayContext, entries) -> {
                    entries.add(ModItems.MODULARIUM);
                    entries.add(ModItems.LEVEL1_ACTIVATOR);
                    entries.add(ModItems.MODULAR_PICKAXE);
                    entries.add(ModItems.MODULAR_AXE);
                    entries.add(ModItems.MODULAR_HOE);
                    entries.add(ModItems.MODULAR_SHOVEL);
                    entries.add(ModBlocks.MODULARIUM_ORE);
                    entries.add(ModBlocks.LEVEL1_BLOCK);
                    entries.add(ModBlocks.LEVEL2_BLOCK);
                    entries.add(ModBlocks.LEVEL3_BLOCK);
                    entries.add(ModBlocks.LEVEL4_BLOCK);
                    entries.add(ModBlocks.LEVEL5_BLOCK);
                    entries.add(ModBlocks.LEVEL6_BLOCK);
                    entries.add(ModBlocks.LEVEL7_BLOCK);
                    entries.add(ModBlocks.LEVEL8_BLOCK);
                    entries.add(ModBlocks.MODULAR_INFUSION_CONTROLLER2);
                    entries.add(ModBlocks.MODULAR_INFUSION_DRONE2);
                    entries.add(ModBlocks.DEEPSLATE_MODULARIUM_ORE);
                    entries.add(ModBlocks.MODULAR_INFUSION_CONTROLLER);
                    entries.add(ModBlocks.MODULAR_INFUSION_DRONE);
                    ModRegistries.MODULE_REGISTRY.forEach(item -> {
                        entries.add(Registries.ITEM.get(item.id()));
                    });
                })).build()
        );

        public static void registerAll() {}
    }
    public static class ModCommands {
        private static SuggestionProvider<ServerCommandSource> registerSuggestionProvider(String idString, SuggestionProvider<CommandSource> provider) {
            return SuggestionProviders.register(Identifier.of(ModularToolsMod.MODID, idString), provider);
        }

        public static final SuggestionProvider<ServerCommandSource> MODULE_SUGGESTION_PROVIDER = registerSuggestionProvider("modules", ((context, builder) ->
                CommandSource.suggestFromIdentifier(
                        ModRegistries.MODULE_REGISTRY.stream(),
                        builder,
                        Module::id,
                        module -> Text.literal(module.id().getPath()))
                )
        );

        public static void registerAll() {
            CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
                ModuleCommand.register(dispatcher, registryAccess);
                RepairCommand.register(dispatcher, registryAccess);
            });
        }
    }
    public static class ModScreens {
        private static <T extends ScreenHandler> ScreenHandlerType<T> registerScreenHandler(String id, ScreenHandlerType.Factory<T> factory) {
            return Registry.register(Registries.SCREEN_HANDLER, Identifier.of(ModularToolsMod.MODID, id), new ScreenHandlerType<>(factory, FeatureFlags.VANILLA_FEATURES));
        }
        private static <T extends ScreenHandler, D> ExtendedScreenHandlerType<T, D> registerExtendedScreenHandler(String id, ExtendedScreenHandlerType<T, D> extendedScreenHandlerType) {
            Registry.register(Registries.SCREEN_HANDLER, Identifier.of(ModularToolsMod.MODID, id), extendedScreenHandlerType);
            return extendedScreenHandlerType;

        }

        // public static final ScreenHandlerType<ModularToolScreenHandler> MODULAR_TOOL_SCREEN_HANDLER = registerScreenHandler("modular_tool", ModularToolScreenHandler::new);
        public static final ExtendedScreenHandlerType<ModularToolScreenHandler, ModularToolScreenHandler.Data> MODULAR_TOOL_SCREEN_HANDLER_EXTENDED_SCREEN_HANDLER
                = registerExtendedScreenHandler("modular_tool", new ExtendedScreenHandlerType<>(ModularToolScreenHandler::new, ModularToolScreenHandler.Data.PACKET_CODEC));

        public static void registerAll() {
        }
    }
    public static class ModNetworking {
        public static final Identifier MODULAR_TOOL_PACKET_ID = Identifier.of(ModularToolsMod.MODID, "modular_tool_packet");
        public static void registerAll() {
            PayloadTypeRegistry.playC2S().register(ModularToolSyncPayload.ID, ModularToolSyncPayload.PACKET_CODEC);
            ServerPlayNetworking.registerGlobalReceiver(ModularToolSyncPayload.ID, (payload, context) -> {
                UUID uuid = payload.uuid();
                ModularToolComponentRecord record = payload.record();
                PlayerEntity player = context.player().getServerWorld().getPlayerByUuid(uuid);
                assert player != null;
                player.getMainHandStack().set(ModComponents.MODULAR_TOOL_COMPONENT, record);
            });
        }
    }

    public static void registerAll() {
        ModNetworking.registerAll();
        ModRegistries.registerAll();
        ModComponents.registerAll();
        ModScreens.registerAll();
        ModBlocks.registerAll();
        ModItems.registerAll();
        ModModules.registerAll();
        ModBlockEntities.registerAll();
        ModItemGroups.registerAll();

        BlockBreakDirectionListener.register();
        ModCommands.registerAll();
    }

}
