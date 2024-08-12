package io.github.rfmineguy.modulartools.blocks.modular_infusion_drone2;

import io.github.rfmineguy.modulartools.blocks.modular_infusion_controller_2.ModularInfusionController2BlockEntity;
import io.github.rfmineguy.modulartools.util.MultiblockUtil;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ModularInfusionDrone2Block extends Block implements BlockEntityProvider {
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
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        // Find the nearest controller and set this block's controller to that
        MultiblockUtil.getNearbyController(world, pos).ifSuccess(controller -> {
            if (world.getBlockEntity(pos) instanceof ModularInfusionDrone2BlockEntity thisDrone) {
                thisDrone.setConnectedController(controller);
                controller.addDrone(thisDrone);
            }
        }).ifFailure(s -> {
            if (placer != null) placer.sendMessage(Text.literal(Formatting.YELLOW + "Warning: " + Formatting.BLACK + s));
        });
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        MultiblockUtil.getNearbyController(world, pos).ifSuccess(controller -> {
            if (world.getBlockEntity(pos) instanceof ModularInfusionDrone2BlockEntity thisDrone) {
                controller.removeDrone(thisDrone);
                // controller.tryRemoveDrone(thisDrone).ifSuccess(
                //         // great. its removed
                // ).orElse();
            }
        }).ifFailure(s -> {
            // player.sendMessage(Text.literal(Formatting.YELLOW + "Warning: " + Formatting.BLACK + s));
        });
        return super.onBreak(world, pos, state, player);
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
                return mbe.tryInsertModularLevelBlock(player.getMainHandStack()).mapSuccess(itemStack -> {
                    player.setStackInHand(player.getActiveHand(), itemStack);
                    if (mbe.hasConnectedController()) {
                        assert mbe.getConnectedController() != null;
                        mbe.getConnectedController().updateModularLevel();
                    }
                    return ActionResult.SUCCESS;
                }).orElseMap(insertLevelBlockError -> {
                    player.sendMessage(Text.literal(insertLevelBlockError.toString()), true);
                    return ActionResult.CONSUME;
                });
            } else {
                return mbe.tryExtractModularLevelItem().mapSuccess(itemStack -> {
                    player.setStackInHand(player.getActiveHand(), itemStack);
                    if (mbe.hasConnectedController()) {
                        assert mbe.getConnectedController() != null;
                        mbe.getConnectedController().updateModularLevel();
                    }
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
