package io.github.rfmineguy.modulartools.blocks.infusion1.modular_infusion_drone;

import io.github.rfmineguy.modulartools.blocks.infusion1.modular_infusion_controller.ModularInfusionControllerBlockEntity;
import io.github.rfmineguy.modulartools.util.MultiblockUtil;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
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

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (world.getBlockEntity(pos) instanceof ModularInfusionDroneBlockEntity) {
            BlockPos controller = MultiblockUtil.getNearbyControllerBlockPosition(world, pos);
            if (controller != null && world.getBlockEntity(controller) instanceof ModularInfusionControllerBlockEntity controllerBlockEntity) {
                controllerBlockEntity.addDrone(pos);
            }
        }
        super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof ModularInfusionDroneBlockEntity mbe)) return super.onBreak(world, pos, state, player);

        // drop the active item
        world.spawnEntity(new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, mbe.getActiveStack()));

        // remove drone from controller
        ModularInfusionControllerBlockEntity controllerBlock = mbe.getController();
        if (controllerBlock != null)
            controllerBlock.removeDrone(pos);
        return super.onBreak(world, pos, state, player);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ModularInfusionDroneBlockEntity(pos, state);
    }
}
