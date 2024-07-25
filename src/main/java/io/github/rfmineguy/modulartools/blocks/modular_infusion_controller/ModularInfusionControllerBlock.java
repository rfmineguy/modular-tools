package io.github.rfmineguy.modulartools.blocks.modular_infusion_controller;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class ModularInfusionControllerBlock extends Block implements BlockEntityProvider {
    public ModularInfusionControllerBlock() {
        super(Settings.copy(Blocks.COBBLESTONE));
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.union(
                VoxelShapes.cuboid(0.1875, 0, 0.1875, 0.8125, 0.0625, 0.8125),
                VoxelShapes.cuboid(0.3125, 0.0625, 0.3125, 0.6875, 0.4375, 0.6875),
                VoxelShapes.cuboid(0.25, 0.4375, 0.25, 0.75, 0.5, 0.75)
        );
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ModularInfusionControllerBlockEntity(pos, state);
    }
}
