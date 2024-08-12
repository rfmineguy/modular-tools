package io.github.rfmineguy.modulartools.items;

import io.github.rfmineguy.modulartools.ModRegistration;
import io.github.rfmineguy.modulartools.components.ModularToolComponentRecord;
import io.github.rfmineguy.modulartools.components.tooltip_data.ModularToolTooltipData;
import io.github.rfmineguy.modulartools.modules.MiningSizeModule;
import io.github.rfmineguy.modulartools.modules.Module;
import io.github.rfmineguy.modulartools.util.RaycastUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.Optional;

public class ModularPickaxeItem extends PickaxeItem implements ModularTool {
    public ModularPickaxeItem(ToolMaterial material, Settings settings) {
        super(material, settings);
    }

    boolean isModuleInstalled(ModularToolComponentRecord component, Module module) {
        return component.getModuleIds().contains(ModRegistration.ModRegistries.MODULE_REGISTRY.getId(module));
    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        return Optional.ofNullable((ModularToolComponentRecord) stack.get(ModRegistration.ModComponents.MODULAR_TOOL_COMPONENT)).map(ModularToolTooltipData::new);
    }

    @Override
    public float getMiningSpeed(ItemStack stack, BlockState state) {
        float defaultSpeed = super.getMiningSpeed(stack, state);
        ModularToolComponentRecord modularToolComponent = stack.get(ModRegistration.ModComponents.MODULAR_TOOL_COMPONENT);
        if (modularToolComponent == null) return defaultSpeed;
        if (isModuleInstalled(modularToolComponent, ModRegistration.ModModules.MINING_SPEED_TWO)) return defaultSpeed * 3;
        if (isModuleInstalled(modularToolComponent, ModRegistration.ModModules.MINING_SPEED_ONE)) return defaultSpeed * 2;
        return defaultSpeed;
    }

    public static Vec3i getMiningSize(ItemStack stack) {
        if (!stack.contains(ModRegistration.ModComponents.MODULAR_TOOL_COMPONENT)) return Vec3i.ZERO;
        ModularToolComponentRecord component = stack.get(ModRegistration.ModComponents.MODULAR_TOOL_COMPONENT);
        return component.getSizeModifier();
    }

    public static Pair<BlockPos, BlockPos> getMiningCorners(ItemStack stack, BlockPos pos, Direction direction) {
        ModularToolComponentRecord componentRecord = stack.get(ModRegistration.ModComponents.MODULAR_TOOL_COMPONENT);
        assert componentRecord != null;
        Vec3i miningSize = componentRecord.getSizeModifier();
        System.out.println(miningSize);
        int halfWidth = miningSize.getX() / 2;
        int halfHeight = miningSize.getY() / 2;
        int halfDepth = miningSize.getZ() / 2;
        // System.out.println(halfWidth + ", " + halfHeight + ", " + halfDepth);
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

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return super.use(world, user, hand);
    }

    private static void breakBlock(World world, PlayerEntity player, BlockPos pos) {
        if (world.breakBlock(pos, true)) {
        }
    }

    public static void miningSizeHandler(World world, PlayerEntity player, BlockPos pos) {
        ItemStack pickaxeItem = player.getMainHandStack();
        Direction direction = RaycastUtil.GetBlockSide(player);
        System.out.println(direction);
        Pair<BlockPos, BlockPos> area = getMiningCorners(pickaxeItem, pos, direction);

        System.out.println(area.getLeft() + " " + area.getRight());
        for (int i = area.getLeft().getX(); i <= area.getRight().getX(); i++) {
            for (int j = area.getLeft().getY(); j <= area.getRight().getY(); j++) {
                for (int k = area.getLeft().getZ(); k <= area.getRight().getZ(); k++) {
                    breakBlock(world, player, new BlockPos(i, j, k));
                }
            }
        }
    }

    public static void addModule(ItemStack stack, Module module) {
        ModularToolComponentRecord componentRecord = stack.get(ModRegistration.ModComponents.MODULAR_TOOL_COMPONENT);
        assert componentRecord != null;
        componentRecord = componentRecord.appendModule(module.getRegistryId());
        stack.set(ModRegistration.ModComponents.MODULAR_TOOL_COMPONENT, componentRecord);
    }

    public static void removeModule(ItemStack stack, Module module) {
        ModularToolComponentRecord componentRecord = stack.get(ModRegistration.ModComponents.MODULAR_TOOL_COMPONENT);
        assert componentRecord != null;
        componentRecord = componentRecord.removeModule(module.getRegistryId());
        stack.set(ModRegistration.ModComponents.MODULAR_TOOL_COMPONENT, componentRecord);
    }
}