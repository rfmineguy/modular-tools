package io.github.rfmineguy.modulartools.modules;

import io.github.rfmineguy.modulartools.ModularLevel;
import io.github.rfmineguy.modulartools.Registration;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.ToolMaterials;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public abstract class Module {
    public static Module NONE = new Module(ModularLevel.ZERO) {
        @Override
        public boolean fitsInTool(ItemStack stack) {
            return false;
        }

        @Override
        public void perform(World world, PlayerEntity player, ItemStack toolItem) {

        }
    };

    ModularLevel level;

    public Module(ModularLevel level) {
        this.level = level;
    }

    public abstract boolean fitsInTool(ItemStack stack);
    public abstract void perform(World world, PlayerEntity player, ItemStack toolItem);
    public Identifier getRegistryId() {
        return Registration.MODULE_REGISTRY.getId(this);
    }

    public ModularLevel getLevel() {
        return level;
    }
}
