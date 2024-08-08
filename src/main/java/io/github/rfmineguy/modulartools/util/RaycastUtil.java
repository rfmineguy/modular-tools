package io.github.rfmineguy.modulartools.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

public class RaycastUtil {
    public static BlockHitResult RaycastBlock(PlayerEntity player) {
        HitResult hit = player.raycast(20.0F, 0.0F, false);
        if (hit.getType() == HitResult.Type.BLOCK) {
            return (BlockHitResult) hit;
        }
        return null;
    }
}
