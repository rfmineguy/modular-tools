package io.github.rfmineguy.modulartools.blocks.modular_infusion_drone;

import io.github.rfmineguy.modulartools.Registration;
import io.github.rfmineguy.modulartools.blocks.PedestalBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class ModularInfusionDroneBlockEntity extends PedestalBlockEntity {
    public ModularInfusionDroneBlockEntity(BlockPos pos, BlockState state) {
        super(Registration.MODULAR_INFUSION_DRONE_BLOCK_ENTITY, pos, state);
    }

    @Override
    protected boolean isItemAllowed(ItemStack stack) {
        Identifier id = Registries.ITEM.getId(stack.getItem());
        return Registration.MODULE_REGISTRY.containsId(id);
    }
}
