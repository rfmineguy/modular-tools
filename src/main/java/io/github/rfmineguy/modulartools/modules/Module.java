package io.github.rfmineguy.modulartools.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public abstract class Module {
    public static Module NONE = new Module() {
        @Override
        boolean fitsInTool(ItemStack stack) {
            return false;
        }

        @Override
        void perform(World world, PlayerEntity player, ItemStack toolItem) {

        }
    };

    abstract boolean fitsInTool(ItemStack stack);
    abstract void perform(World world, PlayerEntity player, ItemStack toolItem);
}
