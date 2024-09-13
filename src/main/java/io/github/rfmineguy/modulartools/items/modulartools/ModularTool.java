package io.github.rfmineguy.modulartools.items.modulartools;

import net.minecraft.item.ItemStack;

public interface ModularTool {
    float getModularMiningSpeed(ItemStack stack, float defaultSpeed);
}
