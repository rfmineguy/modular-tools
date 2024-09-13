package io.github.rfmineguy.modulartools.blocks.infusion3.controller;

import com.leakyabstractions.result.api.Result;
import com.leakyabstractions.result.core.Results;
import io.github.rfmineguy.modulartools.ModRegistration;
import io.github.rfmineguy.modulartools.ModularToolsMod;
import io.github.rfmineguy.modulartools.blocks.infusion3.drone.InfusionDroneBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class InfusionControllerBlockEntity extends BlockEntity implements BlockEntityTicker<InfusionControllerBlockEntity> {
    private boolean isActive;
    private boolean shouldAnimateDrones;
    private float t, angle;
    private final List<BlockPos> dronePositions;

    private boolean isDrone(BlockPos pos) {
        if (world == null) return false;
        return world.getBlockEntity(pos) instanceof InfusionDroneBlockEntity;
    }
    public InfusionControllerBlockEntity(BlockPos pos, BlockState state) {
        super(ModRegistration.ModBlockEntities.INFUSION_CONTROLLER_BE, pos, state);
        isActive = false;
        shouldAnimateDrones = false;
        t = 0;
        dronePositions = new ArrayList<>();
        dronePositions.add(pos.west(1));
        dronePositions.add(pos.east(1));
        dronePositions.add(pos.south(1));
        dronePositions.add(pos.north(1));
        dronePositions.add(pos.west(1).north(1));
        dronePositions.add(pos.east(1).north(1));
        dronePositions.add(pos.west(1).south(1));
        dronePositions.add(pos.east(1).south(1));
    }

    public Result<String, Pair<BlockPos, String>> tryFormMultiBlock() {
        if (world == null) return Results.failure(new Pair<>(BlockPos.ORIGIN, "World null"));
        float angleOffset = 0;
        for (BlockPos pos : dronePositions) {
            if (!isDrone(pos)) return Results.failure(new Pair<>(pos, "Incomplete"));
            if (world.getBlockEntity(pos) instanceof InfusionDroneBlockEntity ibe) {
                ibe.setController(this);
                ibe.setAngleOffset(angleOffset);
                angleOffset += (float) (Math.PI * 2 / 8.0);
            }
        }

        isActive = true;
        shouldAnimateDrones = true;
        t = 0;
        return Results.success("");
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state, InfusionControllerBlockEntity blockEntity) {
        angle += 0.1f;
        if (angle > Math.PI * 2) angle = 0;
        if (shouldAnimateDrones) {
            t += 0.01f;
            for (BlockPos p : dronePositions) {
                if (world.getBlockEntity(p) instanceof InfusionDroneBlockEntity ibe) {
                    ibe.animateTowardController(t, this.pos);
                }
            }
            if (t >= 0.5) shouldAnimateDrones = false;
        }
    }

    public float getAngle() {
        return angle;
    }
}
