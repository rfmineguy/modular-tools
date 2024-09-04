package io.github.rfmineguy.modulartools.events.listeners;

import io.github.rfmineguy.modulartools.events.custom.BlockBreakDirectionEvent;
import io.github.rfmineguy.modulartools.items.modulartools.ModularPickaxeItem;
import io.github.rfmineguy.modulartools.items.modulartools.ModularShovelItem;
import net.minecraft.util.ActionResult;

public class BlockBreakDirectionListener {
    public static void register() {
        BlockBreakDirectionEvent.EVENT.register((world, player, direction, pos) -> {
            if (player.getMainHandStack().getItem() instanceof ModularPickaxeItem) {
                ModularPickaxeItem.breakHandler(world, player, pos);
                return ActionResult.PASS;
            }
            if (player.getMainHandStack().getItem() instanceof ModularShovelItem){
                ModularShovelItem.breakHandler(world, player, pos);
                return ActionResult.PASS;
            }
            return ActionResult.SUCCESS;
        });
    }
}
