package io.github.rfmineguy.modulartools.jade;

import io.github.rfmineguy.modulartools.ModularToolsMod;
import io.github.rfmineguy.modulartools.blocks.infusion2.modular_infusion_drone2.ModularInfusionDrone2BlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class ModularInfusionDroneComponentProvider implements IBlockComponentProvider {
    public static final ModularInfusionDroneComponentProvider INSTANCE = new ModularInfusionDroneComponentProvider();

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        BlockEntity blockEntity = blockAccessor.getBlockEntity();
        if (blockEntity instanceof ModularInfusionDrone2BlockEntity drone) {
            iTooltip.add(Text.translatable("tooltip.drone.level"));
            iTooltip.append(Text.of(Text.literal(" : ") + drone.getModularLevel().toString()));
        }
    }

    @Override
    public Identifier getUid() {
        return Identifier.of(ModularToolsMod.MODID, "drone_component_provider");
    }
}
