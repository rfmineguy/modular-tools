package io.github.rfmineguy.modulartools.components.tooltip_data;

import io.github.rfmineguy.modulartools.components.ActivatorComponentRecord;
import net.minecraft.item.tooltip.TooltipData;

public record ActivatorTooltipData(ActivatorComponentRecord componentRecord) implements TooltipData {
    public ActivatorComponentRecord component() {
        return componentRecord;
    }
}
