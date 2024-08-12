package io.github.rfmineguy.modulartools.events.listeners;

import io.github.rfmineguy.modulartools.events.custom.BlockBreakDirectionEvent;
import io.github.rfmineguy.modulartools.items.ModularPickaxeItem;
import io.github.rfmineguy.modulartools.util.RaycastUtil;
import net.minecraft.util.ActionResult;

public class BlockBreakDirectionListener {
    public static void register() {
        BlockBreakDirectionEvent.EVENT.register((world, player, direction, pos) -> {
            if (!(player.getMainHandStack().getItem() instanceof ModularPickaxeItem)) return ActionResult.PASS;
            ModularPickaxeItem.miningSizeHandler(world, player, pos);
            RaycastUtil.GetBlockSide(player);
            return ActionResult.SUCCESS;
        });
    }
}
