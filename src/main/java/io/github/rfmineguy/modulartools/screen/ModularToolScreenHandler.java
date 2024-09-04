package io.github.rfmineguy.modulartools.screen;

import io.github.rfmineguy.modulartools.ModRegistration;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.screen.ScreenHandler;

public class ModularToolScreenHandler extends ScreenHandler {
    protected ItemStack stack;
    protected PlayerEntity player;

    public record Data(ItemStack stack) {
        public static final PacketCodec<RegistryByteBuf, Data> PACKET_CODEC = PacketCodec.tuple(ItemStack.PACKET_CODEC, Data::stack, Data::new);
    }
    public ModularToolScreenHandler(int i, PlayerInventory playerInventory, Object o) {
        super(ModRegistration.ModScreens.MODULAR_TOOL_SCREEN_HANDLER_EXTENDED_SCREEN_HANDLER, i);
        if (o instanceof Data) stack = ((Data) o).stack;
        else stack = ItemStack.EMPTY; // simply to prevent null crashes
        this.player = playerInventory.player;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return null;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
