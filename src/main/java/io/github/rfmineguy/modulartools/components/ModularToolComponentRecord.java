package io.github.rfmineguy.modulartools.components;

import com.leakyabstractions.result.api.Result;
import com.leakyabstractions.result.core.Results;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.rfmineguy.modulartools.ModuleCategory;
import io.github.rfmineguy.modulartools.modules.Module;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketDecoder;
import net.minecraft.network.codec.PacketEncoder;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

import java.util.*;

public record ModularToolComponentRecord(Map<Identifier, ModuleData> modules, int broken) {
    public record ModuleData(Module module, int enabled) {
        public static final Codec<ModularToolComponentRecord.ModuleData> CODEC = RecordCodecBuilder.create(instance -> {
            return instance.group(
                    Module.CODEC.fieldOf("module").forGetter(ModularToolComponentRecord.ModuleData::module),
                    Codecs.NONNEGATIVE_INT.fieldOf("enabled").forGetter(ModularToolComponentRecord.ModuleData::enabled)
            ).apply(instance, ModularToolComponentRecord.ModuleData::new);
        });
        public static final PacketCodec<ByteBuf, ModuleData> PACKET_CODEC = new PacketCodec<ByteBuf, ModuleData>() {
            @Override
            public ModuleData decode(ByteBuf buf) {
                return new ModuleData(Module.PACKET_CODEC.decode(buf), buf.readInt());
            }

            @Override
            public void encode(ByteBuf buf, ModuleData value) {
                Module.PACKET_CODEC.encode(buf, value.module());
                buf.writeInt(value.enabled());
            }
        };
        public static ModuleData from(Module module) {
            return new ModuleData(module, 0);
        }

        public ModuleData toggle() {
            // ModularToolsMod.LOGGER.info("Toggled: %s [from: %s, to: %s]".formatted(module.id(), enabled == 1 ? "Enabled" : "Disabled", enabled == 1 ? "Disabled" : "Enabled"));
            return new ModuleData(module, enabled == 0 ? 1 : 0);
        }
        public ModuleData enable() {
            return new ModuleData(module, 1);
        }
        public ModuleData disable() {
            return new ModuleData(module, 0);
        }
        public boolean isEnabled() {
            return enabled == 1;
        }
    }

    public static final ModularToolComponentRecord DEFAULT;
    public static final Codec<ModularToolComponentRecord> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                Codec.unboundedMap(Identifier.CODEC, ModularToolComponentRecord.ModuleData.CODEC).fieldOf("modules").forGetter(ModularToolComponentRecord::modules),
                Codecs.NONNEGATIVE_INT.fieldOf("broken").forGetter(ModularToolComponentRecord::broken)
        ).apply(instance, ModularToolComponentRecord::new);
    });
    public static final PacketCodec<RegistryByteBuf, ModularToolComponentRecord> PACKET_CODEC = new PacketCodec<RegistryByteBuf, ModularToolComponentRecord>() {
        @Override
        public ModularToolComponentRecord decode(RegistryByteBuf buf) {
            PacketDecoder<PacketByteBuf, Identifier> keyDecoder = new PacketDecoder<PacketByteBuf, Identifier>() {
                @Override
                public Identifier decode(PacketByteBuf buf) {
                    return Identifier.PACKET_CODEC.decode(buf);
                }
            };
            PacketDecoder<PacketByteBuf, ModuleData> valueDecoder = new PacketDecoder<PacketByteBuf, ModuleData>() {
                @Override
                public ModuleData decode(PacketByteBuf buf) {
                    return ModuleData.PACKET_CODEC.decode(buf);
                }
            };
            return new ModularToolComponentRecord(buf.readMap(keyDecoder, valueDecoder), buf.readInt());
        }

        @Override
        public void encode(RegistryByteBuf buf, ModularToolComponentRecord value) {
            PacketEncoder<PacketByteBuf, Identifier> keyEncoder = new PacketEncoder<PacketByteBuf, Identifier>() {
                @Override
                public void encode(PacketByteBuf buf, Identifier value) {
                    buf.writeIdentifier(value);
                }
            };
            PacketEncoder<PacketByteBuf, ModuleData> valueEncoder = new PacketEncoder<PacketByteBuf, ModuleData>() {
                @Override
                public void encode(PacketByteBuf buf, ModularToolComponentRecord.ModuleData value) {
                    Module.PACKET_CODEC.encode(buf, value.module());
                    buf.writeInt(value.enabled());
                }
            };
            buf.writeMap(value.modules(), keyEncoder, valueEncoder);
            buf.writeInt(value.broken);
        }
    };

    public ModularToolComponentRecord() {
        this(new HashMap<>(), 0);
    }

    public ModularToolComponentRecord addModule(Module module) {
        if (modules.containsKey(module.id())) return this;
        Map<Identifier, ModuleData> newMap = new HashMap<>(modules);
        newMap.put(module.id(), ModuleData.from(module));
        return new ModularToolComponentRecord(newMap, broken);
    }
    public ModularToolComponentRecord removeModule(Module module) {
        if (!modules.containsKey(module.id())) return this;
        Map<Identifier, ModularToolComponentRecord.ModuleData> newMap = new HashMap<>(modules);
        newMap.remove(module.id());
        return new ModularToolComponentRecord(newMap, broken);
    }
    public ModularToolComponentRecord toggleModule(Module module) {
        if (!modules.containsKey(module.id())) return this;
        Map<Identifier, ModularToolComponentRecord.ModuleData> newMap = new HashMap<>(modules);
        newMap.put(module.id(), newMap.get(module.id()).toggle());
        return new ModularToolComponentRecord(newMap, broken);
    }
    public ModularToolComponentRecord disableCategoryExcept(ModuleCategory category, Module curr) {
        HashMap<Identifier, ModuleData> copy = new HashMap<>();
        for (Map.Entry<Identifier, ModuleData> entry : modules.entrySet()) {
            if (entry.getValue().module.getCategoryEnum() == category)
                copy.put(entry.getKey(), entry.getValue().disable());
            else
                copy.put(entry.getKey(), entry.getValue());
        }
        return new ModularToolComponentRecord(copy, broken);
    }
    public ModularToolComponentRecord setBroken(boolean state) {
        return new ModularToolComponentRecord(modules, state ? 1 : 0);
    }
    public ModuleData getModuleData(Module module) {
        return modules.get(module.id());
    }
    public Result<ModuleData, String> getHighestEnabledModuleByCategory(ModuleCategory category) {
        ModuleData highest = null;
        for (ModuleData data : modules.values()) {
            if (data.module.getCategoryEnum() != category) continue;
            if (!data.isEnabled()) continue;
            if (highest == null || data.module.category() > highest.module.category()) {
                highest = data;
            }
        }
        if (highest == null) return Results.failure("");
        return Results.success(highest);
    }
    public boolean hasModule(Module module) {
        return modules.containsKey(module.id());
    }
    public boolean isModuleEnabled(Module module) {
        if (!modules.containsKey(module.id())) return false;
        return modules.get(module.id()).isEnabled();
    }
    public boolean isBroken() {
        return broken == 1;
    }

    static {
        DEFAULT = new ModularToolComponentRecord();
    }
}