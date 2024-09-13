package io.github.rfmineguy.modulartools.items;

import io.github.rfmineguy.modulartools.ModRegistration;
import io.github.rfmineguy.modulartools.components.tooltip_data.ActivatorTooltipData;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;

import java.util.Optional;

public class ActivatorItem extends Item {
    public ActivatorItem(Settings settings) {
        super(settings);
    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        return Optional.ofNullable(stack.get(ModRegistration.ModComponents.ACTIVATOR_COMPONENT)).map(ActivatorTooltipData::new);
    }
}
