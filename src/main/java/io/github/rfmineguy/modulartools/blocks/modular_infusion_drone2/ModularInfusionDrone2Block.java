package io.github.rfmineguy.modulartools.blocks.modular_infusion_drone2;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ModularInfusionDrone2Block extends Block implements BlockEntityProvider {
    private static class PlaceLevelBlockError {
        public static PlaceLevelBlockError EMPTY_ITEM(ItemStack stack) {
            return new PlaceLevelBlockError("Empty item");
        }
        public static PlaceLevelBlockError NOT_A_LEVEL_BLOCK(ItemStack stack) {
            return new PlaceLevelBlockError("Not a level block");
        }
        private final String formatted;

        PlaceLevelBlockError(String formatted) {
            this.formatted = formatted;
        }

        @Override
        public String toString() {
            return formatted;
        }
    }

    public ModularInfusionDrone2Block() {
        super(Settings.copy(Blocks.COBBLESTONE));
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.union(
                VoxelShapes.cuboid(0.0625, 0, 0.0625, 0.9375, 0.125, 0.9375),
                VoxelShapes.cuboid(0.125, 0.125, 0.75, 0.25, 0.6875, 0.875),
                VoxelShapes.cuboid(0.75, 0.125, 0.75, 0.875, 0.6875, 0.875),
                VoxelShapes.cuboid(0.125, 0.125, 0.125, 0.25, 0.6875, 0.25),
                VoxelShapes.cuboid(0.75, 0.125, 0.125, 0.875, 0.6875, 0.25),
                VoxelShapes.cuboid(0.0625, 0.625, 0.0625, 0.9375, 0.75, 0.9375)
        );
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ModularInfusionDrone2BlockEntity(pos, state);
    }

    private ActionResult sneakingAction(ModularInfusionDrone2BlockEntity droneEntity, PlayerEntity player, BlockHitResult hit) {
        System.out.println("Sneaking action");
        return ActionResult.SUCCESS;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!(world.getBlockEntity(pos) instanceof ModularInfusionDrone2BlockEntity mbe)) return ActionResult.PASS;
        double y = hit.getPos().y;
        double ydec = y - (int) y; // decimal part of y
        if (player.isSneaking()) {
            return sneakingAction(mbe, player, hit);
        }
        if (ydec > 0.6) {
            if (!player.getMainHandStack().isEmpty()) {
                return mbe.tryInsertModuleItem(player.getMainHandStack()).mapSuccess(itemStack -> {
                    player.setStackInHand(player.getActiveHand(), itemStack);
                    return ActionResult.SUCCESS;
                }).orElseMap(insertModuleItemError -> {
                    player.sendMessage(Text.literal(insertModuleItemError.toString()), true);
                    return ActionResult.CONSUME;
                });
            }
            else {
                ItemStack module = mbe.extractModule();
                player.setStackInHand(player.getActiveHand(), module);
                return ActionResult.SUCCESS;
            }
        }
        else {
            if (!player.getMainHandStack().isEmpty()) {
                return mbe.tryInsertLevelBlock(player.getMainHandStack()).mapSuccess(itemStack -> {
                    player.setStackInHand(player.getActiveHand(), itemStack);
                    return ActionResult.SUCCESS;
                }).orElseMap(insertLevelBlockError -> {
                    player.sendMessage(Text.literal(insertLevelBlockError.toString()), true);
                    return ActionResult.CONSUME;
                });
            } else {
                return mbe.tryExtractLevelItem().mapSuccess(itemStack -> {
                    player.setStackInHand(player.getActiveHand(), itemStack);
                    return ActionResult.SUCCESS;
                }).orElseMap(extractLevelItemError -> {
                    player.sendMessage(Text.literal(extractLevelItemError.toString()), true);
                    return ActionResult.CONSUME;
                });
            }
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return ((world1, pos, state1, blockEntity) -> {
           if (blockEntity instanceof ModularInfusionDrone2BlockEntity drone2BlockEntity) {
               drone2BlockEntity.tick(world, pos, state1, drone2BlockEntity);
           }
        });
    }
}
