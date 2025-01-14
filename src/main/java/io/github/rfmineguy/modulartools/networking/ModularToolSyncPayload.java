package io.github.rfmineguy.modulartools.networking;

import io.github.rfmineguy.modulartools.ModRegistration;
import io.github.rfmineguy.modulartools.components.ModularToolComponentRecord;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

import java.util.UUID;

public record ModularToolSyncPayload(ModularToolComponentRecord record, UUID uuid) implements CustomPayload {
    public static final CustomPayload.Id<ModularToolSyncPayload> ID = new Id<>(ModRegistration.ModNetworking.MODULAR_TOOL_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, ModularToolSyncPayload> PACKET_CODEC = new PacketCodec<>() {
        @Override
        public ModularToolSyncPayload decode(RegistryByteBuf buf) {
            return new ModularToolSyncPayload(ModularToolComponentRecord.PACKET_CODEC.decode(buf), UUID.fromString(PacketCodecs.STRING.decode(buf)));
        }

        @Override
        public void encode(RegistryByteBuf buf, ModularToolSyncPayload value) {
            ModularToolComponentRecord.PACKET_CODEC.encode(buf, value.record);
            PacketCodecs.STRING.encode(buf, value.uuid.toString());
        }
    };

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static ServerPlayNetworking.PlayPayloadHandler<ModularToolSyncPayload> MODULAR_TOOL_PACKET = new ServerPlayNetworking.PlayPayloadHandler<ModularToolSyncPayload>() {
        @Override
        public void receive(ModularToolSyncPayload payload, ServerPlayNetworking.Context context) {
            UUID uuid = payload.uuid();
            ModularToolComponentRecord record = payload.record();
            PlayerEntity player = context.player().getServerWorld().getPlayerByUuid(uuid);
            assert player != null;
            player.getMainHandStack().set(ModRegistration.ModComponents.MODULAR_TOOL_COMPONENT, record);
        }
    };
}
