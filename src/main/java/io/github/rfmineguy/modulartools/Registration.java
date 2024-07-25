package io.github.rfmineguy.modulartools;

import io.github.rfmineguy.modulartools.blocks.modular_infusion_controller.ModularInfusionControllerBlock;
import io.github.rfmineguy.modulartools.blocks.modular_infusion_controller.ModularInfusionControllerBlockEntity;
import io.github.rfmineguy.modulartools.blocks.modular_infusion_drone.ModularInfusionDroneBlockEntity;
import io.github.rfmineguy.modulartools.blocks.modular_infusion_drone.ModularInfusionDroneBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class Registration {
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
                BlockEntityType.Builder.create(factory, block).build()
        );
    }

    // public static final Item MODULAR_TOOL = registerItem(new ModularToolItem(), "modular_tool");
    public static final Item MODULARIUM                = registerItem("modularium", new Item(new Item.Settings()));

    // public static final Block MODULAR_BENCH            = registerBlock("modular_bench_2", new ModularBenchBlock());
    public static final Block MODULARIUM_ORE           = registerBlock("modularium_ore", new Block(AbstractBlock.Settings.copy(Blocks.DIAMOND_ORE)));
    public static final Block DEEPSLATE_MODULARIUM_ORE = registerBlock("deepslate_modularium_ore", new Block(AbstractBlock.Settings.copy(Blocks.DIAMOND_ORE)));

    public static final Block MODULAR_INFUSION_CONTROLLER = registerBlock("modular_infusion_controller", new ModularInfusionControllerBlock());
    public static final Block MODULAR_INFUSION_DRONE      = registerBlock("modular_infusion_drone", new ModularInfusionDroneBlock());

    public static final BlockEntityType<ModularInfusionControllerBlockEntity> MODULAR_INFUSION_CONTROLLER_BLOCK_ENTITY_BLOCK
            = registerBlockEntity("modular_infusion_controller_block_entity",
                                MODULAR_INFUSION_CONTROLLER,
                                ModularInfusionControllerBlockEntity::new);
    public static final BlockEntityType<ModularInfusionDroneBlockEntity> MODULAR_INFUSION_DRONE_BLOCK_ENTITY_BLOCK
            = registerBlockEntity("modular_infusion_drone_block_entity",
                                MODULAR_INFUSION_DRONE,
                                ModularInfusionDroneBlockEntity::new);

    public static void registerAll() {
    }
}
