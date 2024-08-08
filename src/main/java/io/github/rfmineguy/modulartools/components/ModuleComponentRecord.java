package io.github.rfmineguy.modulartools.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.rfmineguy.modulartools.ModularLevel;
import io.github.rfmineguy.modulartools.modules.Module;
import net.minecraft.util.dynamic.Codecs;

public record ModuleComponentRecord(int level) {
    public static final ModuleComponentRecord DEFAULT;
    public static final Codec<ModuleComponentRecord> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                Codecs.POSITIVE_INT.fieldOf("level").forGetter(ModuleComponentRecord::level)
        ).apply(instance, ModuleComponentRecord::new);
    });

    public static ModuleComponentRecord ofLevel(ModularLevel level) {
        return new ModuleComponentRecord(level.ordinal());
    }

    public ModularLevel getLevel() {
        return ModularLevel.values()[level];
    }

    static {
        DEFAULT = new ModuleComponentRecord(-1);
    }
}
