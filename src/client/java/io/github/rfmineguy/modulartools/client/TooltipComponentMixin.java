package io.github.rfmineguy.modulartools.client;

import io.github.rfmineguy.modulartools.components.tooltip_data.LevelBlockTooltipData;
import io.github.rfmineguy.modulartools.tooltip.LevelBlockTooltipComponent;
import io.github.rfmineguy.modulartools.tooltip.ModularToolTooltipComponent;
import io.github.rfmineguy.modulartools.components.tooltip_data.ModularToolTooltipData;
import io.github.rfmineguy.modulartools.tooltip.ModuleTooltipComponent;
import io.github.rfmineguy.modulartools.components.tooltip_data.ModuleTooltipData;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.tooltip.TooltipData;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TooltipComponent.class)
public interface TooltipComponentMixin {
    @Debug
    @Inject(method = "of(Lnet/minecraft/item/tooltip/TooltipData;)Lnet/minecraft/client/gui/tooltip/TooltipComponent;", at = @At(value = "HEAD"), cancellable = true)
    private static void of(TooltipData data, CallbackInfoReturnable<TooltipComponent> cir) {
        if (data instanceof LevelBlockTooltipData levelBlockTooltipData) {
            cir.setReturnValue(new LevelBlockTooltipComponent(levelBlockTooltipData.component()));
        }
        if (data instanceof ModuleTooltipData moduleTooltipData) {
            cir.setReturnValue(new ModuleTooltipComponent(moduleTooltipData.component()));
        }
        if (data instanceof ModularToolTooltipData modularTooltipData) {
            cir.setReturnValue(new ModularToolTooltipComponent(modularTooltipData.component()));
        }
    }
}
