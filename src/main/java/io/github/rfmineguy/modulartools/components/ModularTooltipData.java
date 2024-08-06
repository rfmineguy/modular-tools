package io.github.rfmineguy.modulartools.components;

import net.minecraft.item.tooltip.TooltipData;

public record ModualrTooltipData(ModularToolComponentRecord component) implements TooltipData {
    public ModualrTooltipData(ModularToolComponentRecord component) {
        this.component = component;
    }

    @Override
    public ModularToolComponentRecord component() {
        return component;
    }
}
