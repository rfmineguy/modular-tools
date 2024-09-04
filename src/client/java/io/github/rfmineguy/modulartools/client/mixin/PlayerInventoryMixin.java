package io.github.rfmineguy.modulartools.client.mixin;

import io.github.rfmineguy.modulartools.ModRegistration;
import io.github.rfmineguy.modulartools.events.custom.ModularToolScrollEvent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Nameable;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Debug(export = true)
@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin implements Inventory, Nameable {
    @Shadow @Final public PlayerEntity player;

    @Inject(method = "scrollInHotbar", at = @At(value = "INVOKE", target = "Ljava/lang/Math;signum(D)D"), cancellable = true)
    private void scrollInHotBar(double scrollAmount, CallbackInfo ci) {
        assert this.player != null;
        boolean isSneaking = this.player.isSneaking();
        ItemStack stack = this.player.getMainHandStack();
        boolean isHoldingModularTool = stack.contains(ModRegistration.ModComponents.MODULAR_TOOL_COMPONENT);

        if (isSneaking && isHoldingModularTool) {
            ModularToolScrollEvent.EVENT.invoker().onScroll(scrollAmount, this.player);
            ci.cancel();
        }
    }
}
