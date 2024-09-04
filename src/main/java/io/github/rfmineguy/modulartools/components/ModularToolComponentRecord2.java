package io.github.rfmineguy.modulartools.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.rfmineguy.modulartools.ModuleCategory;
import io.github.rfmineguy.modulartools.modules.Module;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketDecoder;
import net.minecraft.network.codec.PacketEncoder;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

import java.util.HashMap;
import java.util.Map;

public record ModularToolComponentRecord2(Map<Identifier, ModuleData> modules) {
    public record ModuleData(Module module, int enabled) {
        public static final Codec<ModuleData> CODEC = RecordCodecBuilder.create(instance -> {
            return instance.group(
                    Module.CODEC.fieldOf("module").forGetter(ModuleData::module),
                    Codecs.NONNEGATIVE_INT.fieldOf("enabled").forGetter(ModuleData::enabled)
            ).apply(instance, ModuleData::new);
        });
        public static final PacketCodec<RegistryByteBuf, ModuleData> PACKET_CODEC = new PacketCodec<RegistryByteBuf, ModuleData>() {
            @Override
            public ModuleData decode(RegistryByteBuf buf) {
                return new ModuleData(Module.PACKET_CODEC.decode(buf), buf.readInt());
            }

            @Override
            public void encode(RegistryByteBuf buf, ModuleData value) {
                Module.PACKET_CODEC.encode(buf, value.module);
                buf.writeInt(value.enabled);
            }
        };
        public static ModuleData from(Module module) {
            return new ModuleData(module, 0);
        }

        public ModuleData toggle() {
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

    public static final Codec<ModularToolComponentRecord2> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                Codec.unboundedMap(Identifier.CODEC, ModuleData.CODEC).fieldOf("modules").forGetter(ModularToolComponentRecord2::modules)
        ).apply(instance, ModularToolComponentRecord2::new);
    });
    public static final PacketCodec<RegistryByteBuf, ModularToolComponentRecord2> PACKET_CODEC = new PacketCodec<RegistryByteBuf, ModularToolComponentRecord2>() {
        @Override
        public ModularToolComponentRecord2 decode(RegistryByteBuf buf) {
            return null;
        }

        @Override
        public void encode(RegistryByteBuf buf, ModularToolComponentRecord2 value) {
            PacketEncoder<PacketByteBuf, Identifier> keyEncoder = new PacketEncoder<PacketByteBuf, Identifier>() {
                @Override
                public void encode(PacketByteBuf buf, Identifier value) {
                    buf.writeIdentifier(value);
                }
            };
            PacketEncoder<PacketByteBuf, ModuleData> valueEncoder = new PacketEncoder<PacketByteBuf, ModuleData>() {
                @Override
                public void encode(PacketByteBuf buf, ModuleData value) {
                    Module.PACKET_CODEC.encode(buf, value.module);
                    buf.writeInt(value.enabled);
                }
            };
            buf.writeMap(value.modules, keyEncoder, valueEncoder);
        }
    };

    public ModularToolComponentRecord2() {
        this(new HashMap<>()); // force the map to be a hashmap
    }

    public ModularToolComponentRecord2 addModule(Module module) {
        if (modules.containsKey(module.id())) return this;
        Map<Identifier, ModuleData> newMap = new HashMap<>(modules);
        newMap.put(module.id(), ModuleData.from(module));
        return new ModularToolComponentRecord2(newMap);
    }

    public ModularToolComponentRecord2 removeModule(Module module) {
        if (!modules.containsKey(module.id())) return this;
        Map<Identifier, ModuleData> newMap = new HashMap<>(modules);
        newMap.remove(module.id());
        return new ModularToolComponentRecord2(newMap);
    }

    public ModularToolComponentRecord2 toggleModule(Module module) {
        if (!modules.containsKey(module.id())) return this;
        Map<Identifier, ModuleData> newMap = new HashMap<>(modules);
        newMap.put(module.id(), newMap.get(module.id()).toggle());
        return new ModularToolComponentRecord2(newMap);
    }

    public boolean hasModule(Module module) {
        return modules.containsKey(module.id());
    }

    public boolean isModuleEnabled(Module module) {
        if (!modules.containsKey(module.id())) return false;
        return modules.get(module.id()).isEnabled();
    }

    public boolean isCategoryInstalled(ModuleCategory category) {
        for (Map.Entry<Identifier, ModuleData> data : modules.entrySet()) {
            if (data.getValue().module.getCategoryEnum() == category) return true;
        }
        return false;
    }
}
