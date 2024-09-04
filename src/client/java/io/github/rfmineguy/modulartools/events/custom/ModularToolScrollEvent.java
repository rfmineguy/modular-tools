package io.github.rfmineguy.modulartools.events.custom;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;

public class ModularToolScrollEvent {
    public static final Event<ModularToolScrollCallback> EVENT = EventFactory.createArrayBacked(ModularToolScrollCallback.class,
            (listeners) -> (scrollAmount, player) -> {
                boolean cancel = false;
                for (ModularToolScrollCallback event : listeners) {
                    if (event.onScroll(scrollAmount, player)) {
                        cancel = true;
                    };
                }
                return cancel;
            });

    public interface ModularToolScrollCallback {
        boolean onScroll(double scrollAmount, PlayerEntity player);
    }
}
