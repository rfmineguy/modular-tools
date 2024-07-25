package io.github.rfmineguy.modulartools.blocks.modular_infusion_drone;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ModularInfusionDroneBlock extends Block implements BlockEntityProvider {
    public ModularInfusionDroneBlock() {
        super(Settings.copy(Blocks.COBBLESTONE));
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.union(
                VoxelShapes.cuboid(0.125, 0, 0.125, 0.8125, 0.125, 0.875),
                VoxelShapes.cuboid(0.1875, 0.125, 0.1875, 0.75, 0.1875, 0.8125)
        );
    }

    void reportState(ModularInfusionDroneBlockEntity mbe) {
        System.out.println(mbe.getActiveStack());
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        BlockEntity be = world.getBlockEntity(pos);
        ItemStack activeItem = player.getEquippedStack(EquipmentSlot.MAINHAND);
        if (!(be instanceof ModularInfusionDroneBlockEntity mbe)) return ActionResult.PASS;
        if (player.getActiveHand() != Hand.MAIN_HAND) return ActionResult.PASS; // must be main hand
        if (player.isSneaking()) {
            reportState(mbe);
            return ActionResult.SUCCESS;
        }
        if (!player.isSneaking()) {
            if (!activeItem.isEmpty()) {
                ItemStack item = mbe.putItem(activeItem);
                player.setStackInHand(player.getActiveHand(), item);
            }
            else {
                ItemStack item = mbe.pullItem();
                player.setStackInHand(player.getActiveHand(), item);
            }
            return ActionResult.SUCCESS;
        }
        return super.onUse(state, world, pos, player, hit);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ModularInfusionDroneBlockEntity(pos, state);
    }
}
