package io.github.rfmineguy.modulartools.blocks.modular_infusion_controller;

import io.github.rfmineguy.modulartools.ModRegistration;
import io.github.rfmineguy.modulartools.blocks.modular_infusion_drone.ModularInfusionDroneBlockEntity;
import io.github.rfmineguy.modulartools.util.MultiblockUtil;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;

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

    void reportState(ModularInfusionControllerBlockEntity mbe) {
        System.out.println(mbe.getActiveStack());
        System.out.println(mbe.getDrones());
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        BlockEntity be = world.getBlockEntity(pos);
        ItemStack heldItem = player.getEquippedStack(EquipmentSlot.MAINHAND);
        if (!(be instanceof ModularInfusionControllerBlockEntity mbe)) return ActionResult.PASS;
        if (player.getActiveHand() != Hand.MAIN_HAND) return ActionResult.PASS; // must be main hand
        if (player.isSneaking()) {
            reportState(mbe);
            return ActionResult.SUCCESS;
        }
        if (!heldItem.isEmpty()) {
            return mbe.tryInsertModularTool(heldItem).mapSuccess(itemStack -> {
                player.setStackInHand(player.getActiveHand(), itemStack);
                return ActionResult.SUCCESS;
            }).orElseMap(insertToolError -> {
                player.sendMessage(Text.literal(insertToolError.toString()), true);
                return ActionResult.PASS;
            });
        }
        else {
            return mbe.tryInfuseModularTool(heldItem).mapSuccess(itemStack -> {
                player.sendMessage(Text.literal("Modified tool"), true);
                player.setStackInHand(player.getActiveHand(), itemStack);
                return ActionResult.SUCCESS;
            }).orElseMap(infuseToolError -> {
                player.sendMessage(Text.literal(infuseToolError.toString()), true);
                return ActionResult.PASS;
            });
        }
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (world.getBlockEntity(pos) instanceof ModularInfusionControllerBlockEntity controllerBlockEntity) {
            HashSet<BlockPos> drones = MultiblockUtil.getNearbyDronePositions(world, pos);
            controllerBlockEntity.setDrones(drones);
            controllerBlockEntity.notifyChanged();
        }
        super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof ModularInfusionControllerBlockEntity mbe)) return super.onBreak(world, pos, state, player);
        for (BlockPos drone : mbe.getDrones()) {
            if (world.getBlockEntity(drone) instanceof ModularInfusionDroneBlockEntity droneBlockEntity) {
                droneBlockEntity.clearController();
            }
        }
        world.spawnEntity(new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, mbe.getActiveStack()));
        return super.onBreak(world, pos, state, player);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ModularInfusionControllerBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (type != ModRegistration.ModBlockEntities.MODULAR_INFUSION_CONTROLLER_BLOCK_ENTITY) return null;
        return ModularInfusionControllerBlockEntity::tick;
    }
}
