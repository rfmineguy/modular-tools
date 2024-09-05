package io.github.rfmineguy.modulartools.modules;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.rfmineguy.modulartools.ModRegistration;
import io.github.rfmineguy.modulartools.ModularLevel;
import io.github.rfmineguy.modulartools.ModuleCategory;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

public record Module(int level, int category, Identifier id) {
    public static final Codec<Module> CODEC = RecordCodecBuilder.create(moduleInstance -> {
        return moduleInstance.group(
                Codecs.NONNEGATIVE_INT.fieldOf("level").forGetter(Module::level),
                Codecs.NONNEGATIVE_INT.fieldOf("category").forGetter(Module::category),
                Identifier.CODEC.fieldOf("id").forGetter(Module::id)
        ).apply(moduleInstance, Module::new);
    });
    public static final PacketCodec<ByteBuf, Module> PACKET_CODEC = new PacketCodec<ByteBuf, Module>() {
        @Override
        public Module decode(ByteBuf buf) {
            return new Module(buf.readInt(), buf.readInt(), Identifier.PACKET_CODEC.decode(buf));
        }

        @Override
        public void encode(ByteBuf buf, Module value) {
            buf.writeInt(value.level);
            buf.writeInt(value.category);
            Identifier.PACKET_CODEC.encode(buf, value.id);
        }
    };

    public static Module of(ModularLevel level, ModuleCategory category) {
        return new Module(level.ordinal(), category.ordinal(), Identifier.of(""));
    }
    public Module setId(Identifier id) {
        return new Module(level, category, id);
    }
    public ModularLevel getLevelEnum() {
        return ModularLevel.values()[level];
    }
    public ModuleCategory getCategoryEnum() {
        return ModuleCategory.values()[category];
    }
    public CategoryData getCategoryData() {
        if (getCategoryEnum() == ModuleCategory.MINING_SIZE) {
            return switch (getLevelEnum()) {
                case ONE -> MiningSizeCategoryData.THREE_BY_THREE;
                case TWO -> MiningSizeCategoryData.FIVE_BY_FIVE;
                case THREE -> MiningSizeCategoryData.TWENTY_ONE_BY_TWENTY_ONE;
                default -> null;
            };
        }
        if (getCategoryEnum() == ModuleCategory.SPEED) {
            return switch (getLevelEnum()) {
                case ONE -> SpeedCategoryData.ONE;
                case TWO -> SpeedCategoryData.TWO;
                default -> null;
            };
        }
        return null;
    }

    @Override
    public String toString() {
        return "Module {id: %s, level: %s, category: %s}".formatted(id().getPath(), getLevelEnum().name(), getCategoryEnum().name());
    }
    private String upperFirst(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }
    public String humanString() {
        Identifier moduleName = id();
        String[] split = moduleName.getPath().split("\\.");
        if (split.length != 2) {
            System.err.println("Module: " + moduleName + " has incorrect format");
            return "";
        }
        return "%s %s Module".formatted(upperFirst(split[0]), upperFirst(split[1]));
    }


}