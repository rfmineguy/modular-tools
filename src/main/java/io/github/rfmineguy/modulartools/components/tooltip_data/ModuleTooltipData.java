package io.github.rfmineguy.modulartools.components.tooltip_data;

import io.github.rfmineguy.modulartools.components.ModuleComponentRecord;
import net.minecraft.item.tooltip.TooltipData;

public record ModuleTooltipData(ModuleComponentRecord component) implements TooltipData {
    public ModuleTooltipData(ModuleComponentRecord component) {
        this.component = component;
    }

    @Override
    public ModuleComponentRecord component() {
        return component;
    }
}
