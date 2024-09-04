package io.github.rfmineguy.modulartools.events.listeners;

import io.github.rfmineguy.modulartools.ModRegistration;
import io.github.rfmineguy.modulartools.components.ModularToolComponentRecord;
import io.github.rfmineguy.modulartools.events.custom.ModularToolScrollEvent;
import net.minecraft.item.ItemStack;

public class ModularToolScrollListener {
    public static void register() {
        ModularToolScrollEvent.EVENT.register((scrollAmount, player) -> {
            ItemStack stack = player.getMainHandStack();
            ModularToolComponentRecord component = stack.get(ModRegistration.ModComponents.MODULAR_TOOL_COMPONENT);
            assert component != null;
            if (component.modules().isEmpty()) return true;
            // if (scrollAmount < 0) component = component.increaseSelectedModule();
            // if (scrollAmount > 0) component = component.decreaseSelectedModule();
            stack.set(ModRegistration.ModComponents.MODULAR_TOOL_COMPONENT, component);
            return true;
        });
    }
}
