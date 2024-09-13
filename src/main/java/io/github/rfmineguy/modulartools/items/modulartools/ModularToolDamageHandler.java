package io.github.rfmineguy.modulartools.items.modulartools;

import io.github.rfmineguy.modulartools.ModRegistration;
import io.github.rfmineguy.modulartools.components.ModularToolComponentRecord;
import net.fabricmc.fabric.api.item.v1.CustomDamageHandler;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class ModularToolDamageHandler implements CustomDamageHandler {
    @Override
    public int damage(ItemStack stack, int amount, LivingEntity entity, EquipmentSlot slot, Runnable breakCallback) {
        assert stack.contains(ModRegistration.ModComponents.MODULAR_TOOL_COMPONENT);
        ModularToolComponentRecord componentRecord = stack.get(ModRegistration.ModComponents.MODULAR_TOOL_COMPONENT);
        assert componentRecord != null;
        if (componentRecord.hasModule(ModRegistration.ModModules.UNBREAKABLE) && componentRecord.getModuleData(ModRegistration.ModModules.UNBREAKABLE).isEnabled()) return 0;
        if (componentRecord.isBroken()) return 0;
        if (stack.getDamage() + amount >= stack.getMaxDamage()) {
            componentRecord = componentRecord.setBroken(true);
            stack.set(ModRegistration.ModComponents.MODULAR_TOOL_COMPONENT, componentRecord);
            return 0;
        }
        return amount;
    }
}
