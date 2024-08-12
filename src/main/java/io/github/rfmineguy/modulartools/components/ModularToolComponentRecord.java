package io.github.rfmineguy.modulartools.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.rfmineguy.modulartools.ModRegistration;
import io.github.rfmineguy.modulartools.modules.MiningSizeModule;
import io.github.rfmineguy.modulartools.modules.MiningSpeedModule;
import io.github.rfmineguy.modulartools.modules.Module;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;
import java.util.List;

public record ModularToolComponentRecord(List<Identifier> moduleIds) {
    public static final ModularToolComponentRecord DEFAULT;
    public static final Codec<ModularToolComponentRecord> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
            Identifier.CODEC.listOf().fieldOf("module_ids").forGetter(ModularToolComponentRecord::getModuleIds)
        ).apply(instance, ModularToolComponentRecord::new);
    });

    public ModularToolComponentRecord() {
        this(new ArrayList<>(0));
    }

    private ArrayList<Identifier> addId(Identifier id) {
        ArrayList<Identifier> ids = new ArrayList<>(moduleIds);
        ids.add(id);
        return ids;
    }
    private ArrayList<Identifier> removeId(Identifier id) {
        ArrayList<Identifier> ids = new ArrayList<>(moduleIds);
        ids.remove(id);
        return ids;
    }
    private ArrayList<Identifier> copyIds() {
        return new ArrayList<>(moduleIds);
    }
    // Modifier methods
    public ModularToolComponentRecord appendModule(Identifier moduleId) {
        return new ModularToolComponentRecord(addId(moduleId));
    }
    public ModularToolComponentRecord removeModule(Identifier moduleId) {
        return new ModularToolComponentRecord(removeId(moduleId));
    }

    // Getter methods
    public List<Identifier> getModuleIds() {
        return moduleIds;
    }
    public boolean isEmpty() {
        return moduleIds.isEmpty();
    }

    // Modifier getter methods
    public float getSpeedMultiplier() {
        if (moduleIds.contains(ModRegistration.ModModules.MINING_SPEED_ONE.getRegistryId())) {
            return ((MiningSpeedModule) ModRegistration.ModModules.MINING_SPEED_ONE).getLevelMultiplier();
        }
        return 1;
    }
    public Vec3i getSizeModifier() {
        if (containsAll(moduleIds, ModRegistration.ModModules.MINING_SIZE_ONE, ModRegistration.ModModules.MINING_SIZE_TWO, ModRegistration.ModModules.MINING_SIZE_THREE))
            return ((MiningSizeModule) ModRegistration.ModModules.MINING_SIZE_THREE).getSize().asVec();
        if (containsAll(moduleIds, ModRegistration.ModModules.MINING_SIZE_ONE, ModRegistration.ModModules.MINING_SIZE_TWO))
            return ((MiningSizeModule) ModRegistration.ModModules.MINING_SIZE_TWO).getSize().asVec();
        if (containsAll(moduleIds, ModRegistration.ModModules.MINING_SIZE_ONE))
            return ((MiningSizeModule) ModRegistration.ModModules.MINING_SIZE_ONE).getSize().asVec();
        return new Vec3i(1, 1, 1);
    }
    static {
        DEFAULT = new ModularToolComponentRecord();
    }

    private boolean containsAll(List<Identifier> ids, Module... modules) {
        for (Module m : modules) {
            if (!ids.contains(m.getRegistryId())) return false;
        }
        return true;
    }
}