package io.github.rfmineguy.modulartools;

import io.github.rfmineguy.modulartools.blocks.modular_infusion_controller.ModularInfusionControllerBlock;
import io.github.rfmineguy.modulartools.blocks.modular_infusion_controller.ModularInfusionControllerBlockEntity;
import io.github.rfmineguy.modulartools.blocks.modular_infusion_drone.ModularInfusionDroneBlockEntity;
import io.github.rfmineguy.modulartools.blocks.modular_infusion_drone.ModularInfusionDroneBlock;
import io.github.rfmineguy.modulartools.blocks.modular_infusion_drone2.ModularInfusionDrone2Block;
import io.github.rfmineguy.modulartools.blocks.modular_infusion_drone2.ModularInfusionDrone2BlockEntity;
import io.github.rfmineguy.modulartools.components.LevelBlockComponentRecord;
import io.github.rfmineguy.modulartools.components.ModularToolComponentRecord;
import io.github.rfmineguy.modulartools.components.ModuleComponentRecord;
import io.github.rfmineguy.modulartools.entities.ActivatorEntity;
import io.github.rfmineguy.modulartools.items.ActivatorItem;
import io.github.rfmineguy.modulartools.items.LevelBlockItem;
import io.github.rfmineguy.modulartools.items.ModularPickaxeItem;
import io.github.rfmineguy.modulartools.items.ModuleItem;
import io.github.rfmineguy.modulartools.modules.MiningSizeModule;
import io.github.rfmineguy.modulartools.modules.MiningSpeedModule;
import io.github.rfmineguy.modulartools.modules.Module;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentType;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.*;
import net.minecraft.registry.*;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

public class Registration {
    private static final RegistryKey<Registry<Module>> MODULE_REG_KEY = RegistryKey.ofRegistry(Identifier.of(ModularToolsMod.MODID, "modules"));
    public static final Registry<Module> MODULE_REGISTRY = FabricRegistryBuilder.createSimple(MODULE_REG_KEY)
            .attribute(RegistryAttribute.SYNCED)
            .buildAndRegister();

    private static Item registerItem(String idString, Item item) {
       Identifier id  = Identifier.of(ModularToolsMod.MODID, idString);
       return Registry.register(Registries.ITEM, id, item);
    }
    private static Block registerBlock(String idString, Block block) {
        Identifier id = Identifier.of(ModularToolsMod.MODID, idString);
        Block b = Registry.register(Registries.BLOCK, id, block);
        registerItem(idString, new BlockItem(b, new Item.Settings()));
        return b;
    }
    private static <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(String idString, Block block, BlockEntityType.BlockEntityFactory<T> factory) {
        return Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                Identifier.of(ModularToolsMod.MODID, idString),
                BlockEntityType.Builder.create(factory, block).build(null)
        );
    }
    private static Module registerModuleAndItem(String idString, ModularLevel level, Module module) {
        registerItem(idString, new ModuleItem(new Item.Settings().component(MODULE_COMPONENT, ModuleComponentRecord.ofLevel(level))));
        return Registry.register(MODULE_REGISTRY,
                Identifier.of(ModularToolsMod.MODID, idString),
                module);
    }
    private static Module registerModuleForExistingItem(String idString, Module module) {
        return Registry.register(MODULE_REGISTRY,
                Identifier.of("minecraft", idString),
                module);
    }
    private static <T> ComponentType<T> registerComponent(String id, Supplier<ComponentType.Builder<T>> componentTypeBuilder) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, id, componentTypeBuilder.get().build());
    }
    private static Block registerLevelBlock(String idString, ModularLevel level, Block block) {
        Identifier id = Identifier.of(ModularToolsMod.MODID, idString);
        Block b = Registry.register(Registries.BLOCK, id, block);
        registerItem(idString, new LevelBlockItem(b, new Item.Settings().component(Registration.LEVEL_BLOCK_COMPONENT, LevelBlockComponentRecord.ofLevel(level))));
        return b;
    }
    private static ActivatorItem registerActivatorItem(String idString, ModularLevel level) {
        Identifier id = Identifier.of(ModularToolsMod.MODID, idString);
        return Registry.register(Registries.ITEM, id, new ActivatorItem(new Item.Settings()));
    }
    private static ItemGroup registerItemGroup(String idString, ItemGroup itemGroup) {
        return Registry.register(Registries.ITEM_GROUP, Identifier.of(ModularToolsMod.MODID, idString), itemGroup);
    }
    private static <T extends Entity> EntityType<T> registerEntityType(String idString, EntityType.Builder<T> builder) {
        return Registry.register(Registries.ENTITY_TYPE, Identifier.of(ModularToolsMod.MODID, idString), builder.build());
    }

    // Components
    public static final ComponentType<ModularToolComponentRecord> MODULAR_TOOL_COMPONENT = registerComponent("modular_tool", () -> {
        return ComponentType.<ModularToolComponentRecord>builder().codec(ModularToolComponentRecord.CODEC);
    });
    public static final ComponentType<LevelBlockComponentRecord> LEVEL_BLOCK_COMPONENT = registerComponent("level_block", () -> {
        return ComponentType.<LevelBlockComponentRecord>builder().codec(LevelBlockComponentRecord.CODEC);
    });
    public static final ComponentType<ModuleComponentRecord> MODULE_COMPONENT = registerComponent("module", () -> {
        return ComponentType.<ModuleComponentRecord>builder().codec(ModuleComponentRecord.CODEC);
    });

    // Modules
    public static final Module MINING_SPEED_ONE           = registerModuleAndItem("speed.one", ModularLevel.ONE, new MiningSpeedModule(ModularLevel.ONE));
    public static final Module MINING_SPEED_TWO           = registerModuleAndItem("speed.two", ModularLevel.TWO, new MiningSpeedModule(ModularLevel.TWO));
    public static final Module MINING_SIZE_ONE            = registerModuleAndItem("3x1.one", ModularLevel.TWO, new MiningSizeModule(ModularLevel.TWO, MiningSizeModule.MiningSize.MINING_SIZE_3X1));

    // Items
    public static final Item MODULARIUM                   = registerItem("modularium", new Item(new Item.Settings()));
    public static final Item MODULAR_PICKAXE              = registerItem("modular_pickaxe", new ModularPickaxeItem(ToolMaterials.IRON, new Item.Settings().component(MODULAR_TOOL_COMPONENT, ModularToolComponentRecord.DEFAULT)));
    public static final ActivatorItem LEVEL1_ACTIVATOR    = registerActivatorItem("level1_activator", ModularLevel.ONE);

    // Blocks
    public static final Block MODULARIUM_ORE              = registerBlock("modularium_ore", new Block(AbstractBlock.Settings.copy(Blocks.DIAMOND_ORE)));
    public static final Block DEEPSLATE_MODULARIUM_ORE    = registerBlock("deepslate_modularium_ore", new Block(AbstractBlock.Settings.copy(Blocks.DIAMOND_ORE)));

    public static final Block MODULAR_INFUSION_CONTROLLER = registerBlock("modular_infusion_controller", new ModularInfusionControllerBlock());
    public static final Block MODULAR_INFUSION_DRONE      = registerBlock("modular_infusion_drone", new ModularInfusionDroneBlock());
    public static final Block MODULAR_INFUSION_DRONE2     = registerBlock("modular_infusion_drone2", new ModularInfusionDrone2Block());

    public static final Block TEST_CONTROLLER             = registerBlock("controller_test", new Block(AbstractBlock.Settings.copy(Blocks.ANVIL)));

    // Activators

    public static final Block LEVEL1_BLOCK                = registerLevelBlock("level_1", ModularLevel.ONE, new Block(AbstractBlock.Settings.copy(Blocks.ANVIL)));
    public static final Block LEVEL2_BLOCK                = registerLevelBlock("level_2", ModularLevel.TWO, new Block(AbstractBlock.Settings.copy(Blocks.ANVIL)));
    public static final Block LEVEL3_BLOCK                = registerLevelBlock("level_3", ModularLevel.THREE, new Block(AbstractBlock.Settings.copy(Blocks.ANVIL)));
    public static final Block LEVEL4_BLOCK                = registerLevelBlock("level_4", ModularLevel.FOUR, new Block(AbstractBlock.Settings.copy(Blocks.ANVIL)));
    public static final Block LEVEL5_BLOCK                = registerLevelBlock("level_5", ModularLevel.FIVE, new Block(AbstractBlock.Settings.copy(Blocks.ANVIL)));
    public static final Block LEVEL6_BLOCK                = registerLevelBlock("level_6", ModularLevel.SIX, new Block(AbstractBlock.Settings.copy(Blocks.ANVIL)));
    public static final Block LEVEL7_BLOCK                = registerLevelBlock("level_7", ModularLevel.SEVEN, new Block(AbstractBlock.Settings.copy(Blocks.ANVIL)));
    public static final Block LEVEL8_BLOCK                = registerLevelBlock("level_8", ModularLevel.EIGHT, new Block(AbstractBlock.Settings.copy(Blocks.ANVIL)));

    // BlockEntityTypes
    public static final BlockEntityType<ModularInfusionControllerBlockEntity> MODULAR_INFUSION_CONTROLLER_BLOCK_ENTITY
            = registerBlockEntity("modular_infusion_controller_block_entity",
                                MODULAR_INFUSION_CONTROLLER,
                                ModularInfusionControllerBlockEntity::new);
    public static final BlockEntityType<ModularInfusionDroneBlockEntity> MODULAR_INFUSION_DRONE_BLOCK_ENTITY
            = registerBlockEntity("modular_infusion_drone_block_entity",
                                MODULAR_INFUSION_DRONE,
                                ModularInfusionDroneBlockEntity::new);
    public static final BlockEntityType<ModularInfusionDrone2BlockEntity> MODULAR_INFUSION_DRONE2_BLOCK_ENTITY
            = registerBlockEntity("modular_infusion_drone2_block_entity",
                                MODULAR_INFUSION_DRONE2,
                                ModularInfusionDrone2BlockEntity::new);

    // EntityTypes
    // public static final EntityType<ActivatorEntity> ACTIVATOR_ENTITY_TYPE
    //         = registerEntityType("activator",
    //                             EntityType.Builder.create(ActivatorEntity::new, SpawnGroup.MISC).dimensions(0.5F, 0.5F));

    // Creative Tabs
    public static final ItemGroup MODULAR_TOOLS_ITEM_GROUP = registerItemGroup("main", FabricItemGroup.builder()
            .icon(() -> new ItemStack(MODULARIUM))
            .displayName(Text.translatable("itemGroup.modular_tools.main"))
            .entries(((displayContext, entries) -> {
                entries.add(MODULARIUM);
                entries.add(MODULARIUM_ORE);
                entries.add(LEVEL1_BLOCK);
                entries.add(LEVEL2_BLOCK);
                entries.add(LEVEL3_BLOCK);
                entries.add(LEVEL4_BLOCK);
                entries.add(LEVEL5_BLOCK);
                entries.add(LEVEL6_BLOCK);
                entries.add(LEVEL7_BLOCK);
                entries.add(LEVEL8_BLOCK);
                entries.add(LEVEL1_ACTIVATOR);
                entries.add(MODULAR_INFUSION_DRONE2);
                entries.add(DEEPSLATE_MODULARIUM_ORE);
                entries.add(MODULAR_INFUSION_CONTROLLER);
                entries.add(MODULAR_INFUSION_DRONE);
                entries.add(MODULAR_PICKAXE);
                Registration.MODULE_REGISTRY.forEach(item -> {
                    entries.add(Registries.ITEM.get(item.getRegistryId()));
                });
            })).build()
    );

    public static void registerAll() {
    }
}
