package io.github.rfmineguy.modulartools.blocks.infusion3.controller;

import io.github.rfmineguy.modulartools.items.ModularWrenchItem;
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
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class InfusionControllerBlock extends Block implements BlockEntityProvider {
    private static VoxelShape s = VoxelShapes.union(
            VoxelShapes.cuboid(0.125, 0, 0.125, 0.875, 0.0625, 0.875),
            VoxelShapes.cuboid(0.375, 0.03125, 0.375, 0.625, 0.09375, 0.625)
    );
    public InfusionControllerBlock() {
        super(Settings.copy(Blocks.ANVIL));
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new InfusionControllerBlockEntity(pos, state);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!(stack.getItem() instanceof ModularWrenchItem)) return ItemActionResult.FAIL;
        if (!(world.getBlockEntity(pos) instanceof InfusionControllerBlockEntity be)) return ItemActionResult.FAIL;
        return be.tryFormMultiBlock().mapSuccess((pair) -> {
            player.sendMessage(Text.literal("Construct multiblock"), true);
            return ItemActionResult.SUCCESS;
        }).orElseMap((pair) -> {
            String message = pair.getRight();
            player.sendMessage(Text.literal(message), true);
            return ItemActionResult.SUCCESS;
        });
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return s;
    }

    @Override
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        super.onBroken(world, pos, state);
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        return super.onBreak(world, pos, state, player);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return ((world1, pos, state1, blockEntity) -> {
            if (blockEntity instanceof InfusionControllerBlockEntity infusionBe) {
                infusionBe.tick(world, pos, state1, infusionBe);
            }
        });
    }
}
