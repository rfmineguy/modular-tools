package io.github.rfmineguy.modulartools.items.modulartools;

import io.github.rfmineguy.modulartools.ModRegistration;
import io.github.rfmineguy.modulartools.ModularToolsMod;
import io.github.rfmineguy.modulartools.components.ModularToolComponentRecord;
import io.github.rfmineguy.modulartools.screen.ModularToolScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ModularAxeItem extends AxeItem implements ModularTool, NamedScreenHandlerFactory  {
    public ModularAxeItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
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

    @Override
    public float getMiningSpeed(ItemStack stack, BlockState state) {
        float defaultSpeed = super.getMiningSpeed(stack, state);
        return getModularMiningSpeed(stack, defaultSpeed);
    }

    public static void breakHandler(World world, PlayerEntity player, BlockPos pos) {
        ModularToolComponentRecord componentRecord = player.getMainHandStack().get(ModRegistration.ModComponents.MODULAR_TOOL_COMPONENT);
        assert componentRecord != null;
        ModularToolsMod.LOGGER.warn("ModularAxeItem#breakHandler not implemented");
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Modular Axe");
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new ModularToolScreenHandler(syncId, playerInventory, player.getMainHandStack());
    }
}
