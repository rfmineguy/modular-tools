package io.github.rfmineguy.modulartools.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.rfmineguy.modulartools.ModularLevel;
import net.minecraft.util.dynamic.Codecs;

public record ActivatorComponentRecord(int level) {
    public static final ActivatorComponentRecord DEFAULT = new ActivatorComponentRecord(0);
    public static final Codec<ActivatorComponentRecord> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                Codecs.NONNEGATIVE_INT.fieldOf("level").forGetter(ActivatorComponentRecord::level)
        ).apply(instance, ActivatorComponentRecord::new);
    });

    public static ActivatorComponentRecord ofLevel(ModularLevel level) {
        return new ActivatorComponentRecord(level.ordinal());
    }

    public ModularLevel getLevel() {
        return ModularLevel.values()[level];
    }
}
