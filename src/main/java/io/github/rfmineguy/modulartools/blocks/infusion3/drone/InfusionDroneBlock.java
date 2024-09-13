package io.github.rfmineguy.modulartools.blocks.infusion3.drone;

import io.github.rfmineguy.modulartools.ModRegistration;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class InfusionDroneBlock extends Block implements BlockEntityProvider {
    private VoxelShape s = VoxelShapes.union(
            VoxelShapes.cuboid(0.3125, 0, 0.3125, 0.6875, 0.0625, 0.6875),
            VoxelShapes.cuboid(0.4375, 0.03125, 0.4375, 0.5625, 0.09375, 0.5625)
            );

    public InfusionDroneBlock() {
        super(Settings.copy(Blocks.ANVIL));
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new InfusionDroneBlockEntity(pos, state);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    private ItemActionResult onUseWithActivator(ItemStack stack, PlayerEntity player, Hand hand, InfusionDroneBlockEntity be) {
        return be.tryPutActivator(stack).mapSuccess(item -> {
            player.setStackInHand(hand, item);
            return ItemActionResult.SUCCESS;
        }).orElseMap(fail -> {
            player.sendMessage(Text.literal(fail), true);
            return ItemActionResult.FAIL;
        });
    }

    private ItemActionResult onUseWithLevelBlock(ItemStack stack, PlayerEntity player, Hand hand, InfusionDroneBlockEntity be) {
        return be.tryPutLevelBlock(stack).mapSuccess(item -> {
            player.setStackInHand(hand, item);
            return ItemActionResult.SUCCESS;
        }).orElseMap(fail -> {
            player.sendMessage(Text.literal(fail));
            return ItemActionResult.FAIL;
        });
    }

    private ActionResult onUseWithNoItem(PlayerEntity player, Hand hand, InfusionDroneBlockEntity be) {
        return be.tryRemoveItem().mapSuccess(item -> {
            player.setStackInHand(hand, item);
            return ActionResult.SUCCESS;
        }).orElseMap(fail -> {
            player.sendMessage(Text.literal(fail));
            return ActionResult.FAIL;
        });
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!(world.getBlockEntity(pos) instanceof InfusionDroneBlockEntity ibe)) return ItemActionResult.FAIL;
        if (stack.contains(ModRegistration.ModComponents.ACTIVATOR_COMPONENT)) return onUseWithActivator(stack, player, hand, ibe);
        if (stack.contains(ModRegistration.ModComponents.LEVEL_BLOCK_COMPONENT)) return onUseWithLevelBlock(stack, player, hand, ibe);
        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!(world.getBlockEntity(pos) instanceof InfusionDroneBlockEntity ibe)) return ActionResult.FAIL;
        if (player.getMainHandStack() == ItemStack.EMPTY) return onUseWithNoItem(player, player.getActiveHand(), ibe);
        return super.onUse(state, world, pos, player, hit);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (world.getBlockEntity(pos) instanceof InfusionDroneBlockEntity droneBlockEntity) {
            Vec3d subPos = droneBlockEntity.getSubPosition();
            return s.offset(subPos.x, subPos.y, subPos.z);
        }
        return VoxelShapes.empty();
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return ((world1, pos, state1, blockEntity) -> {
            if (blockEntity instanceof InfusionDroneBlockEntity infusionBe) {
                infusionBe.tick(world, pos, state1, infusionBe);
            }
        });
    }
}
