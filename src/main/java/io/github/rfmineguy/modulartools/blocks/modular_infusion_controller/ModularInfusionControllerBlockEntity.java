package io.github.rfmineguy.modulartools.blocks.modular_infusion_controller;

import io.github.rfmineguy.modulartools.Registration;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class ModularInfusionControllerBlockEntity extends BlockEntity {
    public ModularInfusionControllerBlockEntity(BlockPos pos, BlockState state) {
        super(Registration.MODULAR_INFUSION_CONTROLLER_BLOCK_ENTITY_BLOCK, pos, state);
    }
}
