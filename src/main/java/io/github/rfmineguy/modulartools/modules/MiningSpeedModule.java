package io.github.rfmineguy.modulartools.modules;

import io.github.rfmineguy.modulartools.ModularLevel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.ToolMaterials;
import net.minecraft.world.World;

public class MiningSpeedModule extends Module {
    public MiningSpeedModule(ModularLevel level) {
        super(level);
    }

    @Override
    public boolean fitsInTool(ItemStack stack) {
        return stack.getItem() instanceof ToolItem;
    }

    @Override
    public void perform(World world, PlayerEntity player, ItemStack toolItem) {

    }

    public float getLevelMultiplier() {
        return switch (level) {
            case ZERO -> 1;
            case ONE -> 1.5f;
            case TWO -> 2;
            case THREE -> 3;
            default -> 0;
        };
    }
}
