package io.github.rfmineguy.modulartools.blocks.infusion2.modular_infusion_controller_2;

import io.github.rfmineguy.modulartools.ModRegistration;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ModularInfusionController2Block extends Block implements BlockEntityProvider {
    public ModularInfusionController2Block() {
        super(AbstractBlock.Settings.copy(Blocks.AMETHYST_BLOCK));
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.union(
                VoxelShapes.cuboid(0.125, 0, 0.125, 0.875, 0.125, 0.875),
                VoxelShapes.cuboid(0.25, 0.125, 0.25, 0.75, 0.25, 0.75),
                VoxelShapes.cuboid(0.3125, 0.875, 0.3125, 0.6875, 0.9375, 0.6875),
                VoxelShapes.cuboid(0.3125, 0.9375, 0.25, 0.6875, 1, 0.3125),
                VoxelShapes.cuboid(0.3125, 0.9375, 0.6875, 0.6875, 1, 0.75),
                VoxelShapes.cuboid(0.25, 0.9375, 0.25, 0.3125, 1, 0.75),
                VoxelShapes.cuboid(0.6875, 0.9375, 0.25, 0.75, 1, 0.75)
        );
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (world.getBlockEntity(pos) instanceof ModularInfusionController2BlockEntity controller2BlockEntity) {
            if (controller2BlockEntity.revalidateMultiBlock()) {
                // do some cool effect here
            }
        }
        super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ModularInfusionController2BlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (type != ModRegistration.ModBlockEntities.MODULAR_INFUSION_CONTROLLER2_BLOCK_ENTITY) return null;
        return ModularInfusionController2BlockEntity::tick;
    }
}
