package io.github.rfmineguy.modulartools.events.listeners;

import io.github.rfmineguy.modulartools.ModRegistration;
import io.github.rfmineguy.modulartools.ModularToolsMod;
import io.github.rfmineguy.modulartools.components.ModularToolComponentRecord;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.texture.Scaling;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteDimensions;
import net.minecraft.client.util.math.Rect2i;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class HudListener {
    public static void register() {
        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
            PlayerEntity player = MinecraftClient.getInstance().player;
            if (player == null) return;
            ItemStack stack = player.getMainHandStack();
            if (!stack.contains(ModRegistration.ModComponents.MODULAR_TOOL_COMPONENT)) return;
            ModularToolComponentRecord componentRecord = stack.get(ModRegistration.ModComponents.MODULAR_TOOL_COMPONENT);
            assert componentRecord != null;
            if (!player.isSneaking()) return;
        });
    }
}
