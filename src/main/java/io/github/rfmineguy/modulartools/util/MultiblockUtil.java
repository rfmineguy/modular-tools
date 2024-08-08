package io.github.rfmineguy.modulartools.util;

import io.github.rfmineguy.modulartools.blocks.modular_infusion_controller.ModularInfusionControllerBlockEntity;
import io.github.rfmineguy.modulartools.blocks.modular_infusion_drone.ModularInfusionDroneBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
}
