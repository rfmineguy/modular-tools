package io.github.rfmineguy.modulartools.items.modulartool.axe;

import com.sun.jna.platform.unix.X11;
import io.github.rfmineguy.modulartools.items.modulartool.ModularTool;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.ToolMaterials;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class ModularAxe extends AxeItem implements ModularTool {
    public ModularAxe(Settings settings) {
        super(ToolMaterials.WOOD, settings);
    }

    @Override
    public ToolMaterial getMaterial() {
        return super.getMaterial();
    }

    @Environment(EnvType.CLIENT)
    protected boolean appendModularTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.literal("[Shift]"));
        return true;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        if (appendModularTooltip(stack, context, tooltip, type)) {
            tooltip.add(Text.literal("Hidden"));
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return super.use(world, user, hand);
    }
}
