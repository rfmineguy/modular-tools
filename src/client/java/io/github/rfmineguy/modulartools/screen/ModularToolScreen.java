package io.github.rfmineguy.modulartools.screen;

import io.github.rfmineguy.modulartools.ModRegistration;
import io.github.rfmineguy.modulartools.ModularToolsMod;
import io.github.rfmineguy.modulartools.ModuleCategory;
import io.github.rfmineguy.modulartools.components.ModularToolComponentRecord;
import io.github.rfmineguy.modulartools.modules.Module;
import io.github.rfmineguy.modulartools.networking.ModularToolSyncPayload;
import io.github.rfmineguy.modulartools.widgets.IdentifiedButtonWidget;
import io.netty.handler.ssl.DelegatingSslContext;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.Rect2i;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class ModularToolScreen extends HandledScreen<ModularToolScreenHandler> {
    private static final Identifier BACKGROUND = Identifier.of(ModularToolsMod.MODID, "textures/gui/modular_tool.png");
    private final int BACKGROUND_WIDTH = 135;
    private final int BACKGROUND_HEIGHT = 65;
    private final PlayerEntity player;

    private static final Rect2i bounds = new Rect2i(0, 0, 0, 0);

    public ModularToolScreen(ModularToolScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.player = inventory.player;
        titleY = 999999;
        playerInventoryTitleY = 999999;
        this.backgroundWidth = 100;
        this.backgroundHeight = 50;
        this.y += 50;
    }
    public void blur() {}
    protected void applyBlur(float delta) {}
    protected void renderDarkening(DrawContext context) {}
    protected void renderDarkening(DrawContext context, int x, int y, int width, int height) {}
    public void renderInGameBackground(DrawContext context) {}

    @Override
    protected void init() {
        int scaledWidth = MinecraftClient.getInstance().getWindow().getScaledWidth();
        int scaledHeight = MinecraftClient.getInstance().getWindow().getScaledHeight();
        int xCenter = scaledWidth / 2;
        int yCenter = scaledHeight / 2;
        System.out.println(scaledWidth);

        bounds.setX(xCenter - BACKGROUND_WIDTH / 2);
        bounds.setY(scaledHeight - BACKGROUND_HEIGHT - 40);
        bounds.setWidth(width);
        bounds.setHeight(height);

        int i = 0;
        int j = 0;
        final ModularToolComponentRecord[] componentRecord = {handler.stack.get(ModRegistration.ModComponents.MODULAR_TOOL_COMPONENT)};
        assert componentRecord[0] != null;
        for (int index = 0; index < ModRegistration.ModRegistries.MODULE_REGISTRY.size(); index++) {
            int finalIndex = index;
            Module curr = ModRegistration.ModRegistries.MODULE_REGISTRY.get(finalIndex);
            addSelectableChild(
                IdentifiedButtonWidget.builder2(button -> {
                    if (!componentRecord[0].hasModule(curr)) {
                        // player.sendMessage(Text.literal(Formatting.RED + "" + curr.humanString() + " not installed"), true);
                        return;
                    }

                    if (componentRecord[0].isModuleEnabled(curr)) {
                        componentRecord[0] = componentRecord[0].toggleModule(curr);
                        handler.stack.set(ModRegistration.ModComponents.MODULAR_TOOL_COMPONENT, componentRecord[0]);
                        return;
                    }

                    componentRecord[0] = componentRecord[0].disableCategoryExcept(curr.getCategoryEnum(), curr);
                    componentRecord[0] = componentRecord[0].toggleModule(curr);
                    handler.stack.set(ModRegistration.ModComponents.MODULAR_TOOL_COMPONENT, componentRecord[0]);
                })
                .size(16, 16)
                .position(bounds.getX() + i * 16 + 4, bounds.getY() + j * 16 + 4)
                .tooltip(Tooltip.of(Text.literal("tooltip")))
                .identifier(curr.id())
                .build()
            );

            if (i != 0 && i % 6 == 0) {
                i = 0;
                j++;
            }
            i++;
        }

        super.init();
    }

    @Override
    public void close() {
        assert MinecraftClient.getInstance().player != null;
        ClientPlayNetworking.send(new ModularToolSyncPayload(handler.stack.get(ModRegistration.ModComponents.MODULAR_TOOL_COMPONENT), MinecraftClient.getInstance().player.getUuid()));
        super.close();
    }

    private void renderModuleItemWithStatus(DrawContext drawContext, Module module, int i, int j) {
        ModularToolComponentRecord componentRecord = handler.stack.get(ModRegistration.ModComponents.MODULAR_TOOL_COMPONENT);
        assert componentRecord != null;
        Identifier identifier = module.id();
        Item item = Registries.ITEM.get(identifier);
        drawContext.drawItem(item.getDefaultStack(), bounds.getX() + i * 16 + 4, bounds.getY() + j * 16 + 4);
        if (!componentRecord.hasModule(module)) {
            drawContext.drawTexture(BACKGROUND, bounds.getX() + i * 16 + 15, bounds.getY() + j * 16 + 2, 151, 135, 15, 7, 7, 256, 256);
        }
        else {
            ModularToolComponentRecord.ModuleData data = componentRecord.getModuleData(module);
            assert data != null; // true via the if/else
            if (data.isEnabled()) {
                drawContext.drawTexture(BACKGROUND, bounds.getX() + i * 16 + 15, bounds.getY() + j * 16 + 2, 151, 135, 8, 7, 7, 256, 256);
            }
            else {
                drawContext.drawTexture(BACKGROUND, bounds.getX() + i * 16 + 15, bounds.getY() + j * 16 + 2, 151, 135, 1, 7, 7, 256, 256);
            }
        }

    }

    void renderTooltips(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        Optional<Element> element = hoveredElement(mouseX, mouseY);
        if (element.isEmpty()) return;
        if (element.isPresent() && element.get() instanceof IdentifiedButtonWidget idButton) {
            Module module = ModRegistration.ModRegistries.MODULE_REGISTRY.get(idButton.getIdentifier());
            drawContext.drawTooltip(textRenderer, List.of(Text.literal(module.humanString())), mouseX, mouseY);
        }
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        drawBackground(drawContext, delta, mouseX, mouseY);
        int i = 0;
        int j = 0;
        for (Module module : ModRegistration.ModRegistries.MODULE_REGISTRY) {
            renderModuleItemWithStatus(drawContext, module, i, j);
            if (i != 0 && i % 6 == 0) {
                i = 0;
                j++;
            }
            i++;
        }
        renderTooltips(drawContext, mouseX, mouseY, delta);
    }

    @Override
    protected void drawBackground(DrawContext drawContext, float delta, int mouseX, int mouseY) {
        drawContext.drawTexture(BACKGROUND, bounds.getX(), bounds.getY(), 0, 0, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
    }
}
