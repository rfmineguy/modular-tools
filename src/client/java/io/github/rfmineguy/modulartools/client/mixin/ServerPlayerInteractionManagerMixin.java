package io.github.rfmineguy.modulartools.client.mixin;

import io.github.rfmineguy.modulartools.events.custom.BlockBreakDirectionEvent;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Debug(export = true)
@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin {
    @Shadow ServerWorld world;
    @Shadow @Final protected ServerPlayerEntity player;

    @Inject(method = "processBlockBreakingAction", at = @At(value = "CONSTANT", args = "stringValue=destroyed"))
    public void blockBreakDirectionSurvival(BlockPos pos, PlayerActionC2SPacket.Action action, Direction direction, int worldHeight, int i, CallbackInfo info) {
        HitResult hitResult = player.raycast(20.f, 0.0f, false);
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHitResult = (BlockHitResult) hitResult;
            BlockBreakDirectionEvent.EVENT.invoker().blockBreak(this.world, this.player, blockHitResult.getSide(), pos);
        }
    }

    @Inject(method = "processBlockBreakingAction", at = @At(value = "CONSTANT", args = "stringValue=creative destroy"))
    public void blockBreakDirectionCreative(BlockPos pos, PlayerActionC2SPacket.Action action, Direction direction, int worldHeight, int sequence, CallbackInfo ci) {
        HitResult hitResult = player.raycast(20.f, 0.0f, false);
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHitResult = (BlockHitResult) hitResult;
            Vec3d hitPos = blockHitResult.getPos().subtract(Vec3d.of(blockHitResult.getBlockPos()));
            BlockBreakDirectionEvent.EVENT.invoker().blockBreak(this.world, this.player, direction, pos);
        }
    }

}
