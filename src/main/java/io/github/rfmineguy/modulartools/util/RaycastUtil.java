package io.github.rfmineguy.modulartools.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class RaycastUtil {
    public static BlockHitResult RaycastBlock(PlayerEntity player) {
        HitResult hit = player.raycast(20.0F, 0.0F, false);
        if (hit.getType() == HitResult.Type.BLOCK) {
            return (BlockHitResult) hit;
        }
        return null;
    }

    public static Direction GetBlockSide(PlayerEntity player) {
        HitResult hit = player.raycast(20.0F, 0.0f, false);
        if (hit.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHitResult = (BlockHitResult) hit;
            Vec3d inBlockPos = blockHitResult.getPos().subtract(blockHitResult.getBlockPos().toCenterPos());
            if (inBlockPos.getX() == -0.5f) return Direction.EAST;
            if (inBlockPos.getX() == 0.5f)  return Direction.WEST;
            if (inBlockPos.getY() == -0.5f) return Direction.DOWN;
            if (inBlockPos.getY() == 0.5f)  return Direction.UP;
            if (inBlockPos.getZ() == 0.5f)  return Direction.NORTH;
            if (inBlockPos.getZ() == -0.5f) return Direction.SOUTH;
        }
        return null;
    }
}
