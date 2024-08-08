package io.github.rfmineguy.modulartools.items;

import io.github.rfmineguy.modulartools.Registration;
import io.github.rfmineguy.modulartools.components.ModularToolComponentRecord;
import io.github.rfmineguy.modulartools.components.tooltip_data.ModularToolTooltipData;
import io.github.rfmineguy.modulartools.modules.Module;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.Optional;

public class ModularPickaxeItem extends PickaxeItem implements ModularTool {
    public ModularPickaxeItem(ToolMaterial material, Settings settings) {
        super(material, settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        return super.useOnBlock(context);
    }

    boolean isModuleInstalled(ModularToolComponentRecord component, Module module) {
        return component.getModuleIds().contains(Registration.MODULE_REGISTRY.getId(module));
    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        return Optional.ofNullable((ModularToolComponentRecord) stack.get(Registration.MODULAR_TOOL_COMPONENT)).map(ModularToolTooltipData::new);
    }

    @Override
    public float getMiningSpeed(ItemStack stack, BlockState state) {
        float defaultSpeed = super.getMiningSpeed(stack, state);
        System.out.println(defaultSpeed);
        ModularToolComponentRecord modularToolComponent = stack.get(Registration.MODULAR_TOOL_COMPONENT);
        if (modularToolComponent == null) return defaultSpeed;
        if (isModuleInstalled(modularToolComponent, Registration.MINING_SPEED_TWO)) {
            return defaultSpeed * 3;
        }
        if (isModuleInstalled(modularToolComponent, Registration.MINING_SPEED_ONE)) {
            return defaultSpeed * 2;
        }
        return defaultSpeed;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return super.use(world, user, hand);
    }
}