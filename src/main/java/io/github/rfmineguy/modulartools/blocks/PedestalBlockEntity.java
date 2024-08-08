package io.github.rfmineguy.modulartools.blocks;

import io.github.rfmineguy.modulartools.blocks.modular_infusion_controller.ModularInfusionControllerBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public abstract class PedestalBlockEntity extends BlockEntity {
    protected DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    protected ModularInfusionControllerBlockEntity controller;
    protected double yAngle = 0;
    private boolean shouldHighlight;
    public PedestalBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void setController(ModularInfusionControllerBlockEntity entity) {
        this.controller = entity;
    }

    public void clearController() {
        this.controller = null;
    }

    public ModularInfusionControllerBlockEntity getController() {
        return controller;
    }

    public ItemStack putItem(ItemStack s) {
        if (!isItemAllowed(s)) return s;
        ItemStack current = inventory.getFirst();
        inventory.set(0, s);
        return current;
    }

    public ItemStack pullItem() {
        ItemStack current = inventory.getFirst();
        inventory.set(0, ItemStack.EMPTY);
        return current;
    }

    public ItemStack getActiveStack() {
        return inventory.getFirst();
    }

    public void setActiveStack(ItemStack stack) {
        inventory.set(0,stack);
    }

    public double getYAngle() {
        return yAngle;
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, inventory, registryLookup);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
        Inventories.readNbt(nbt, this.inventory, registryLookup);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }

    protected abstract boolean isItemAllowed(ItemStack stack);
}
