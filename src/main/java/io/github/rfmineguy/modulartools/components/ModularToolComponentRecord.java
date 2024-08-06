package io.github.rfmineguy.modulartools.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.rfmineguy.modulartools.Registration;
import io.github.rfmineguy.modulartools.modules.MiningSpeedModule;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class ModularToolComponent {
    public static final ModularToolComponent DEFAULT;
    public static final Codec<ModularToolComponent> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
            Identifier.CODEC.listOf().fieldOf("module_ids").forGetter(ModularToolComponent::getModuleIds)
        ).apply(instance, ModularToolComponent::new);
    });
    private final List<Identifier> moduleIds;
    private int tooltipPage;  // used for the tooltip display

    public ModularToolComponent() {
        this.moduleIds = new ArrayList<>();
        this.tooltipPage = 0;
    }
    public ModularToolComponent(List<Identifier> module_ids) {
        this.moduleIds = module_ids;
        this.tooltipPage = 0;
    }
    public void appendModule(Identifier moduleId) {
        this.moduleIds.add(moduleId);
    }
    public List<Identifier> getModuleIds() {
        return moduleIds;
    }
    public boolean isEmpty() {
        return moduleIds.isEmpty();
    }
    public int getTooltipPage() {
        return tooltipPage;
    }
    public void incrementTooltipPage() {
        this.tooltipPage ++;
        if (this.tooltipPage > moduleIds.size()) this.tooltipPage = moduleIds.size() - 1;
    }
    public void decrementTooltipPage() {
        this.tooltipPage --;
        if (this.tooltipPage < 0) this.tooltipPage = 0;

    }
    public float getSpeedMultiplier() {
        if (moduleIds.contains(Registration.MINING_SPEED.getRegistryId())) {
            return ((MiningSpeedModule) Registration.MINING_SPEED).getTierMultiplier();
        }
        return 1;
    }

    static {
        DEFAULT = new ModularToolComponent();
    }
}