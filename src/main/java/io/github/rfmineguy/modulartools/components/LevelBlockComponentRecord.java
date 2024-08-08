package io.github.rfmineguy.modulartools.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.rfmineguy.modulartools.ModularLevel;
import net.minecraft.util.dynamic.Codecs;

public record LevelBlockComponentRecord(int level) {
    public static final LevelBlockComponentRecord DEFAULT;
    public static final Codec<LevelBlockComponentRecord> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                Codecs.POSITIVE_INT.fieldOf("level").forGetter(LevelBlockComponentRecord::level)
        ).apply(instance, LevelBlockComponentRecord::new);
    });

    public static LevelBlockComponentRecord ofLevel(ModularLevel level) {
        return new LevelBlockComponentRecord(level.ordinal());
    }

    public ModularLevel getLevel() {
        return ModularLevel.values()[level];
    }

    static {
        DEFAULT = new LevelBlockComponentRecord(-1);
    }
}
