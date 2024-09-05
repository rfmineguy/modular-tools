package io.github.rfmineguy.modulartools.items.modulartools;

import io.github.rfmineguy.modulartools.ModRegistration;
import io.github.rfmineguy.modulartools.components.ModularToolComponentRecord;
import io.github.rfmineguy.modulartools.components.tooltip_data.ModularToolTooltipData;
import io.github.rfmineguy.modulartools.modules.MiningSizeCategoryData;
import io.github.rfmineguy.modulartools.modules.Module;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;

import java.util.Optional;

public class ModularToolUtil {
    public static boolean canMine(ItemStack itemStack, BlockState state) {
        ModularToolComponentRecord componentRecord = itemStack.get(ModRegistration.ModComponents.MODULAR_TOOL_COMPONENT);
        assert componentRecord != null;
        return !componentRecord.isBroken();
    }
    public static boolean isModuleInstalledAndEnabled(ModularToolComponentRecord record, io.github.rfmineguy.modulartools.modules.Module module)  {
        return record.hasModule(module) && record.isModuleEnabled(module);
    }
    public static Optional<TooltipData> getTooltipData(ItemStack stack) {
        return Optional.ofNullable((ModularToolComponentRecord) stack.get(ModRegistration.ModComponents.MODULAR_TOOL_COMPONENT)).map(ModularToolTooltipData::new);
    }
    public static Pair<BlockPos, BlockPos> getMiningCorners(BlockPos pos, Direction direction, MiningSizeCategoryData data) {
        Vec3i miningSize = data.getSize();
        int halfWidth = miningSize.getX() / 2;
        int halfHeight = miningSize.getY() / 2;
        int halfDepth = miningSize.getZ() / 2;
        return switch (direction) {
            case NORTH, SOUTH ->
                    new Pair<>(
                            new BlockPos(pos.getX() - halfWidth, pos.getY() - halfHeight, pos.getZ() - halfDepth),
                            new BlockPos(pos.getX() + halfWidth, pos.getY() + halfHeight, pos.getZ() + halfDepth));
            case EAST, WEST ->
                    new Pair<>(
                            new BlockPos(pos.getX() - halfDepth, pos.getY() - halfHeight, pos.getZ() - halfWidth),
                            new BlockPos(pos.getX() + halfDepth, pos.getY() + halfHeight, pos.getZ() + halfWidth));
            case UP, DOWN ->
                    new Pair<>(
                            new BlockPos(pos.getX() - halfHeight, pos.getY() - halfDepth, pos.getZ() - halfWidth),
                            new BlockPos(pos.getX() + halfHeight, pos.getY() + halfDepth, pos.getZ() + halfWidth));
            case null, default -> new Pair<>(new BlockPos(pos), new BlockPos(pos));
        };
    }

    // Module interface
    public static void addModule(ItemStack stack, io.github.rfmineguy.modulartools.modules.Module module) {
        ModularToolComponentRecord componentRecord = stack.get(ModRegistration.ModComponents.MODULAR_TOOL_COMPONENT);
        assert componentRecord != null;
        componentRecord = componentRecord.addModule(module);
        stack.set(ModRegistration.ModComponents.MODULAR_TOOL_COMPONENT, componentRecord);
    }
    public static void removeModule(ItemStack stack, io.github.rfmineguy.modulartools.modules.Module module) {
        ModularToolComponentRecord componentRecord = stack.get(ModRegistration.ModComponents.MODULAR_TOOL_COMPONENT);
        assert componentRecord != null;
        componentRecord = componentRecord.removeModule(module);
        stack.set(ModRegistration.ModComponents.MODULAR_TOOL_COMPONENT, componentRecord);
    }
    public static boolean wouldBreak(ItemStack stack, PlayerEntity player, int i) {
        ModularToolComponentRecord componentRecord = stack.get(ModRegistration.ModComponents.MODULAR_TOOL_COMPONENT);
        assert componentRecord != null;
        if (componentRecord.hasModule(ModRegistration.ModModules.UNBREAKABLE)) return false;
        if (componentRecord.isBroken()) return false;
        return stack.getDamage() + i > stack.getMaxDamage();
    }
    public static void damage(ItemStack stack, PlayerEntity player, int i) {
        stack.damage(i, player, EquipmentSlot.MAINHAND);
    }
    public static boolean isModuleInstalled(ItemStack stack, Module module) {
        ModularToolComponentRecord componentRecord = stack.get(ModRegistration.ModComponents.MODULAR_TOOL_COMPONENT);
        assert componentRecord != null;
        return componentRecord.hasModule(module) && componentRecord.isModuleEnabled(module);
    }
}
