package io.github.rfmineguy.modulartools.items.modulartools;

import io.github.rfmineguy.modulartools.ModRegistration;
import io.github.rfmineguy.modulartools.ModuleCategory;
import io.github.rfmineguy.modulartools.components.ModularToolComponentRecord;
import io.github.rfmineguy.modulartools.modules.MiningSizeCategoryData;
import io.github.rfmineguy.modulartools.screen.ModularToolScreenHandler;
import io.github.rfmineguy.modulartools.util.RaycastUtil;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ModularPickaxeItem extends PickaxeItem implements ModularTool, NamedScreenHandlerFactory {
    public ModularPickaxeItem(ToolMaterial material, Settings settings) {
        super(material, settings);
    }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (user.isSneaking() && user instanceof ServerPlayerEntity serverPlayer) {
            var factory = new ExtendedScreenHandlerFactory<ModularToolScreenHandler.Data>() {
                @Override
                public @NotNull ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                    return new ModularToolScreenHandler(syncId, playerInventory, player.getMainHandStack());
                }

                @Override
                public Text getDisplayName() {
                    return Text.literal("Modular Shovel");
                }

                @Override
                public ModularToolScreenHandler.Data getScreenOpeningData(ServerPlayerEntity player) {
                    return new ModularToolScreenHandler.Data(player.getMainHandStack());
                }
            };
            serverPlayer.openHandledScreen(factory);
            return TypedActionResult.success(user.getMainHandStack());
        }
        return super.use(world, user, hand);
    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        return ModularTool.getTooltipData(stack);
    }

    @Override
    public float getMiningSpeed(ItemStack stack, BlockState state) {
        float defaultSpeed = super.getMiningSpeed(stack, state);
        return getModularMiningSpeed(stack, defaultSpeed);
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        if (!ModularTool.canMine(miner.getMainHandStack(), state)) return false;
        return super.canMine(state, world, pos, miner);
    }

    @Override
    public float getModularMiningSpeed(ItemStack stack, float defaultSpeed) {
        ModularToolComponentRecord modularToolComponent = stack.get(ModRegistration.ModComponents.MODULAR_TOOL_COMPONENT);
        if (modularToolComponent == null) return defaultSpeed;
        if (ModularTool.isModuleInstalledAndEnabled(modularToolComponent, ModRegistration.ModModules.MINING_SPEED_TWO)) return defaultSpeed * 3;
        if (ModularTool.isModuleInstalledAndEnabled(modularToolComponent, ModRegistration.ModModules.MINING_SPEED_ONE)) return defaultSpeed * 2;
        return defaultSpeed;
    }

    public static void breakHandler(World world, PlayerEntity player, BlockPos pos) {
        ModularToolComponentRecord componentRecord = player.getMainHandStack().get(ModRegistration.ModComponents.MODULAR_TOOL_COMPONENT);
        assert componentRecord != null;
        componentRecord.getHighestEnabledModuleByCategory(ModuleCategory.MINING_SIZE).ifSuccess(moduleData -> {
            MiningSizeCategoryData data = (MiningSizeCategoryData) moduleData.module().getCategoryData();
            if (moduleData.enabled() == 1) {
                miningSizeBreakHandler(world, player, pos, data);
            }
        });
    }

    private static void miningSizeBreakHandler(World world, PlayerEntity player, BlockPos pos, MiningSizeCategoryData data) {
        Direction direction = RaycastUtil.GetBlockSide(player);
        Pair<BlockPos, BlockPos> area = ModularTool.getMiningCorners(pos, direction, data);

        for (int i = area.getLeft().getX(); i <= area.getRight().getX(); i++) {
            for (int j = area.getLeft().getY(); j <= area.getRight().getY(); j++) {
                for (int k = area.getLeft().getZ(); k <= area.getRight().getZ(); k++) {
                    BlockPos npos = new BlockPos(i, j, k);
                    if (player.getMainHandStack().isSuitableFor(world.getBlockState(npos))) {
                        if (ModularTool.wouldBreak(player.getMainHandStack(), player, 1)) break;
                        if (!player.getMainHandStack().getItem().canMine(world.getBlockState(npos), world, npos, player)) break;
                        if (world.breakBlock(new BlockPos(i, j, k), !player.isCreative())) {
                            player.getMainHandStack().damage(1, player, EquipmentSlot.MAINHAND);
                        }
                    }
                }
            }
        }
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Modular Pickaxe");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new ModularToolScreenHandler(syncId, playerInventory, player.getMainHandStack());
    }
}