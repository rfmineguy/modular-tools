package io.github.rfmineguy.modulartools.items.modulartools;

import io.github.rfmineguy.modulartools.ModRegistration;
import io.github.rfmineguy.modulartools.ModuleCategory;
import io.github.rfmineguy.modulartools.components.ModularToolComponentRecord;
import io.github.rfmineguy.modulartools.components.tooltip_data.ModularToolTooltipData;
import io.github.rfmineguy.modulartools.modules.MiningSizeCategoryData;
import io.github.rfmineguy.modulartools.modules.Module;
import io.github.rfmineguy.modulartools.screen.ModularToolScreenHandler;
import io.github.rfmineguy.modulartools.util.RaycastUtil;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ModularShovelItem extends ShovelItem implements ModularTool {
    public ModularShovelItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        return Optional.ofNullable(stack.get(ModRegistration.ModComponents.MODULAR_TOOL_COMPONENT)).map(ModularToolTooltipData::new);
    }

    @Override
    public float getMiningSpeed(ItemStack stack, BlockState state) {
        return getModularMiningSpeed(stack, super.getMiningSpeed(stack, state));
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
    public ActionResult useOnBlock(ItemUsageContext context) {
        System.out.println("Use on block");
        return useHandler(context);
    }

    @Override
    public float getModularMiningSpeed(ItemStack stack, float defaultSpeed) {
        if (ModularToolUtil.isModuleInstalled(stack, ModRegistration.ModModules.MINING_SPEED_TWO)) return defaultSpeed * 3;
        if (ModularToolUtil.isModuleInstalled(stack, ModRegistration.ModModules.MINING_SPEED_ONE)) return defaultSpeed * 2;
        return defaultSpeed;
    }

    public static boolean canMakePath(Block block) {
        // https://minecraft.fandom.com/wiki/Dirt_Path#Post-generation
        return block == Blocks.DIRT || block == Blocks.GRASS_BLOCK || block == Blocks.COARSE_DIRT || block == Blocks.MYCELIUM ||
                block == Blocks.PODZOL || block == Blocks.ROOTED_DIRT;
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        if (!ModularToolUtil.canMine(miner.getMainHandStack(), state)) return false;
        return super.canMine(state, world, pos, miner);
    }

    private ActionResult useHandler(ItemUsageContext context) {
        assert context.getPlayer() != null;
        ModularToolComponentRecord componentRecord = context.getPlayer().getMainHandStack().get(ModRegistration.ModComponents.MODULAR_TOOL_COMPONENT);
        assert componentRecord != null;
        return componentRecord.getHighestEnabledModuleByCategory(ModuleCategory.MINING_SIZE).mapSuccess(moduleData -> {
           MiningSizeCategoryData data = (MiningSizeCategoryData) moduleData.module().getCategoryData();
           if (data == null) return ActionResult.FAIL;
           if (moduleData.enabled() == 1) {
               return miningSizeTillHandler(context, context.getWorld(), context.getPlayer(), context.getBlockPos(), context.getHand(), data);
           }
           return ActionResult.FAIL;
        }).orElseMap(error -> {
            return super.useOnBlock(context);
        });
    }

    public static void breakHandler(World world, PlayerEntity player, BlockPos pos) {
        ModularToolComponentRecord componentRecord = player.getMainHandStack().get(ModRegistration.ModComponents.MODULAR_TOOL_COMPONENT);
        assert componentRecord != null;
        componentRecord.getHighestEnabledModuleByCategory(ModuleCategory.MINING_SIZE).ifSuccess(moduleData -> {
            System.out.println("Highest: " + moduleData);
            MiningSizeCategoryData data = (MiningSizeCategoryData) moduleData.module().getCategoryData();
            if (moduleData.enabled() == 1) {
                miningSizeBreakHandler(world, player, pos, data);
            }
        });
    }

    private static void miningSizeBreakHandler(World world, PlayerEntity player, BlockPos pos, MiningSizeCategoryData data) {
        Direction direction = RaycastUtil.GetBlockSide(player);
        Pair<BlockPos, BlockPos> area = ModularToolUtil.getMiningCorners(pos, direction, data);

        for (int i = area.getLeft().getX(); i <= area.getRight().getX(); i++) {
            for (int j = area.getLeft().getY(); j <= area.getRight().getY(); j++) {
                for (int k = area.getLeft().getZ(); k <= area.getRight().getZ(); k++) {
                    BlockPos npos = new BlockPos(i, j, k);
                    if (player.getMainHandStack().isSuitableFor(world.getBlockState(npos))) {
                        if (ModularToolUtil.wouldBreak(player.getMainHandStack(), player, 1)) break;
                        if (!player.getMainHandStack().getItem().canMine(world.getBlockState(npos), world, npos, player)) break;
                        if (world.breakBlock(npos, !player.isCreative())) {
                            player.getMainHandStack().damage(1, player, EquipmentSlot.MAINHAND);
                        }
                    }
                }
            }
        }
    }
    private ActionResult miningSizeTillHandler(ItemUsageContext context, World world, PlayerEntity player, BlockPos pos, Hand hand, MiningSizeCategoryData data) {
        Direction direction = RaycastUtil.GetBlockSide(player);
        if (direction != Direction.UP) return ActionResult.FAIL;
        Pair<BlockPos, BlockPos> area = ModularToolUtil.getMiningCorners(pos, direction, data);
        for (int i = area.getLeft().getX(); i <= area.getRight().getX(); i++) {
            for (int k = area.getLeft().getZ(); k <= area.getRight().getZ(); k++) {
                BlockPos newpos = new BlockPos(i, area.getLeft().getY(), k);
                BlockHitResult hit = new BlockHitResult(newpos.toCenterPos(), direction, pos, true);
                ItemUsageContext ctx = new ItemUsageContext(player, hand, hit);
                super.useOnBlock(ctx);
            }
        }
        return ActionResult.SUCCESS;

    }
}
