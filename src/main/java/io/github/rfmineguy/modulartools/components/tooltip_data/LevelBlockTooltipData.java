package io.github.rfmineguy.modulartools.components.tooltip_data;

import io.github.rfmineguy.modulartools.components.LevelBlockComponentRecord;
import net.minecraft.item.tooltip.TooltipData;

public record LevelBlockTooltipData(LevelBlockComponentRecord component) implements TooltipData {
    public LevelBlockTooltipData(LevelBlockComponentRecord component) {
        this.component = component;
    }

    public LevelBlockComponentRecord component() {
        return component;
    }
}
