package io.github.rfmineguy.modulartools.components.tooltip_data;

import io.github.rfmineguy.modulartools.components.ModularToolComponentRecord;
import net.minecraft.item.tooltip.TooltipData;

public record ModularToolTooltipData(ModularToolComponentRecord component) implements TooltipData {
    public ModularToolTooltipData(ModularToolComponentRecord component) {
        this.component = component;
    }

    @Override
    public ModularToolComponentRecord component() {
        return component;
    }
}
