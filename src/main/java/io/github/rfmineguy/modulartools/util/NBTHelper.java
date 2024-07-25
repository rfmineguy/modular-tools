package io.github.rfmineguy.modulartools.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

public class NBTHelper {
    public static NbtCompound writeItemStack(DefaultedList<ItemStack> stacks) {
        NbtCompound compound = new NbtCompound();
        compound.putInt("count", stacks.size());
        NbtList items = new NbtList();
        for (int i = 0; i < stacks.size(); i++) {
            NbtCompound item = new NbtCompound();
            item.putInt("slot", i);
            item.putString("id", Registries.ITEM.getId(stacks.get(i).getItem()).toString());
            item.putInt("count", stacks.get(i).getCount());
            items.add(item);
        }
        compound.put("items", items);
        return compound;
    }

    public static DefaultedList<ItemStack> readItemStack(NbtCompound nbt) {
        NbtElement items = nbt.getList("items", NbtElement.LIST_TYPE);
        int count = nbt.getInt("count");
        DefaultedList<ItemStack> list = DefaultedList.ofSize(count, ItemStack.EMPTY);
        if (!(items instanceof NbtList listItems)) return null;
        for (NbtElement e : listItems) {
            if (e instanceof NbtCompound compound) {
                int slot = compound.getInt("slot");
                Identifier id = Identifier.of(compound.getString("id"));
                list.add(slot, new ItemStack(Registries.ITEM.get(id)));
            }
        }
        return list;
    }
}
