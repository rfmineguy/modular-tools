package io.github.rfmineguy.modulartools.util;

import com.leakyabstractions.result.api.Result;
import com.leakyabstractions.result.core.Results;
import io.github.rfmineguy.modulartools.blocks.infusion1.modular_infusion_controller.ModularInfusionControllerBlockEntity;
import io.github.rfmineguy.modulartools.blocks.infusion2.modular_infusion_controller_2.ModularInfusionController2BlockEntity;
import io.github.rfmineguy.modulartools.blocks.infusion1.modular_infusion_drone.ModularInfusionDroneBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.HashSet;

public class MultiblockUtil {
    private static boolean isDroneBlockEntity(World world, BlockPos pos) {
        return world.getBlockEntity(pos) instanceof ModularInfusionDroneBlockEntity;
    }
    public static HashSet<BlockPos> getNearbyDronePositions(World world, BlockPos pos) {
        HashSet<BlockPos> dronePosList = new HashSet<>();

        // cardinal directions
        if (isDroneBlockEntity(world, pos.west(3)))  dronePosList.add(pos.west(3));
        if (isDroneBlockEntity(world, pos.east(3)))  dronePosList.add(pos.east(3));
        if (isDroneBlockEntity(world, pos.south(3))) dronePosList.add(pos.south(3));
        if (isDroneBlockEntity(world, pos.north(3))) dronePosList.add(pos.north(3));

        // diagonal directions
        if (isDroneBlockEntity(world, pos.west(2).north(2))) dronePosList.add(pos.west(2).north(2));
        if (isDroneBlockEntity(world, pos.east(2).north(2))) dronePosList.add(pos.east(2).north(2));
        if (isDroneBlockEntity(world, pos.west(2).south(2))) dronePosList.add(pos.west(2).south(2));
        if (isDroneBlockEntity(world, pos.east(2).south(2))) dronePosList.add(pos.east(2).south(2));

        return dronePosList;
    }
    private static boolean isControllerBlockEntity(World world, BlockPos pos) {
        return world.getBlockEntity(pos) instanceof ModularInfusionControllerBlockEntity;
    }
    public static BlockPos getNearbyControllerBlockPosition(World world, BlockPos pos) {
        // cardinal directions
        if (isControllerBlockEntity(world, pos.west(3))) return pos.west(3);
        if (isControllerBlockEntity(world, pos.east(3))) return pos.east(3);
        if (isControllerBlockEntity(world, pos.north(3))) return pos.north(3);
        if (isControllerBlockEntity(world, pos.south(3))) return pos.south(3);

        // diagonal directions
        if (isControllerBlockEntity(world, pos.west(2).north(2))) return pos.west(2).north(2);
        if (isControllerBlockEntity(world, pos.east(2).north(2))) return pos.east(2).north(2);
        if (isControllerBlockEntity(world, pos.west(2).south(2))) return pos.west(2).south(2);
        if (isControllerBlockEntity(world, pos.east(2).south(2))) return pos.east(2).south(2);

        return null;
    }

    public static Result<ModularInfusionController2BlockEntity, String> getNearbyController(WorldAccess world, BlockPos pos) {
        if (world.getBlockEntity(pos.west(3)) instanceof ModularInfusionController2BlockEntity mbe) return Results.success(mbe);
        if (world.getBlockEntity(pos.east(3)) instanceof ModularInfusionController2BlockEntity mbe) return Results.success(mbe);
        if (world.getBlockEntity(pos.north(3)) instanceof ModularInfusionController2BlockEntity mbe) return Results.success(mbe);
        if (world.getBlockEntity(pos.south(3)) instanceof ModularInfusionController2BlockEntity mbe) return Results.success(mbe);

        if (world.getBlockEntity(pos.west(2).north(2)) instanceof ModularInfusionController2BlockEntity mbe) return Results.success(mbe);
        if (world.getBlockEntity(pos.west(2).south(2)) instanceof ModularInfusionController2BlockEntity mbe) return Results.success(mbe);
        if (world.getBlockEntity(pos.east(2).north(2)) instanceof ModularInfusionController2BlockEntity mbe) return Results.success(mbe);
        if (world.getBlockEntity(pos.east(2).south(2)) instanceof ModularInfusionController2BlockEntity mbe) return Results.success(mbe);

        return Results.failure("No nearby controller");
    }
}
