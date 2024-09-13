package io.github.rfmineguy.modulartools.blocks.infusion1.modular_infusion_drone;

import io.github.rfmineguy.modulartools.ModRegistration;
import io.github.rfmineguy.modulartools.blocks.PedestalBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class ModularInfusionDroneBlockEntity extends PedestalBlockEntity {
    public ModularInfusionDroneBlockEntity(BlockPos pos, BlockState state) {
        super(ModRegistration.ModBlockEntities.MODULAR_INFUSION_DRONE_BLOCK_ENTITY, pos, state);
    }

    @Override
    protected boolean isItemAllowed(ItemStack stack) {
        Identifier id = Registries.ITEM.getId(stack.getItem());
        return ModRegistration.ModRegistries.MODULE_REGISTRY.containsId(id);
    }
}
