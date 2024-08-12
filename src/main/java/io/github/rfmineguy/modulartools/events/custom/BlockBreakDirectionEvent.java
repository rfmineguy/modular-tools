package io.github.rfmineguy.modulartools.events.custom;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class BlockBreakDirectionEvent {
   public static final Event<BlockBreakDirection> EVENT = EventFactory.createArrayBacked(BlockBreakDirection.class,
           (listeners) -> ((world, player, direction, pos) -> {
               for (BlockBreakDirection listener : listeners) {
                   return listener.blockBreak(world, player, direction, pos);
               }
               return ActionResult.PASS;
           }));

   public interface BlockBreakDirection {
       ActionResult blockBreak(World world, PlayerEntity player, Direction direction, BlockPos pos);
   }
}
