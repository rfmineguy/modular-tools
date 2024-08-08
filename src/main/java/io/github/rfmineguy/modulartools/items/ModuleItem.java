package io.github.rfmineguy.modulartools.items;

import io.github.rfmineguy.modulartools.Registration;
import io.github.rfmineguy.modulartools.components.ModuleComponentRecord;
import io.github.rfmineguy.modulartools.components.tooltip_data.ModuleTooltipData;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;

import java.util.Optional;

public class ModuleItem extends Item {
    public ModuleItem(Settings settings) {
        super(settings);
    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        return Optional.ofNullable(stack.get(Registration.MODULE_COMPONENT)).map(ModuleTooltipData::new);
    }
}
