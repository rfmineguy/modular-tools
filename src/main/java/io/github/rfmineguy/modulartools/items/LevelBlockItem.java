package io.github.rfmineguy.modulartools.items;

import io.github.rfmineguy.modulartools.Registration;
import io.github.rfmineguy.modulartools.components.tooltip_data.LevelBlockTooltipData;
import io.github.rfmineguy.modulartools.components.tooltip_data.ModuleTooltipData;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;

import java.util.Optional;

public class LevelBlockItem extends BlockItem {
    public LevelBlockItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        return Optional.ofNullable(stack.get(Registration.LEVEL_BLOCK_COMPONENT)).map(LevelBlockTooltipData::new);
    }
}