package io.github.rfmineguy.modulartools.modules;

import io.github.rfmineguy.modulartools.ModularLevel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.world.World;

public class MiningSizeModule extends Module {
    public enum MiningSize {
        MINING_SIZE_5X5(5, 5, 1),
        MINING_SIZE_3X3(3, 3, 1),
        MINING_SIZE_3X1(1, 3, 1);

        int dimWidth, dimHeight, dimDepth;
        MiningSize(int dimWidth, int dimHeight, int dimDepth) {
            this.dimWidth = dimWidth;
            this.dimHeight = dimHeight;
            this.dimDepth = dimDepth;
        }
    }

    MiningSize size;

    public MiningSizeModule(ModularLevel level, MiningSize size) {
        super(level);
        this.size = size;
    }

    @Override
    public boolean fitsInTool(ItemStack stack) {
        return stack.getItem() instanceof PickaxeItem || stack.getItem() instanceof ShovelItem;
    }

    @Override
    public void perform(World world, PlayerEntity player, ItemStack toolItem) {
        if (!fitsInTool(toolItem)) return;
    }
}
