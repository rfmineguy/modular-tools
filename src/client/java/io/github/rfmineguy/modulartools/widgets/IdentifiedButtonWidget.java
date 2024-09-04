package io.github.rfmineguy.modulartools.widgets;

import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class IdentifiedButtonWidget extends ButtonWidget {
    protected final Identifier identifier;

    IdentifiedButtonWidget(int x, int y, int width, int height, Identifier identifier, ButtonWidget.PressAction onPress, @Nullable ButtonWidget.NarrationSupplier narrationSupplier) {
        super(x, y, width, height, Text.literal(""), onPress, narrationSupplier);
        this.identifier = identifier;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public static IdentifiedButtonWidget.Builder builder2(ButtonWidget.PressAction onPress) {
        return new IdentifiedButtonWidget.Builder(onPress);
    }

    public static class Builder {
        private Identifier id;
        private final ButtonWidget.PressAction onPress;
        @Nullable
        private Tooltip tooltip;
        private int x;
        private int y;
        private int width = 150;
        private int height = 20;
        private ButtonWidget.NarrationSupplier narrationSupplier = ButtonWidget.DEFAULT_NARRATION_SUPPLIER;

        public Builder(ButtonWidget.PressAction onPress) {
            this.onPress = onPress;
        }

        public IdentifiedButtonWidget.Builder position(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public IdentifiedButtonWidget.Builder width(int width) {
            this.width = width;
            return this;
        }

        public IdentifiedButtonWidget.Builder size(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public IdentifiedButtonWidget.Builder dimensions(int x, int y, int width, int height) {
            return this.position(x, y).size(width, height);
        }

        public IdentifiedButtonWidget.Builder tooltip(@Nullable Tooltip tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        public IdentifiedButtonWidget.Builder narrationSupplier(IdentifiedButtonWidget.NarrationSupplier narrationSupplier) {
            this.narrationSupplier = narrationSupplier;
            return this;
        }

        public IdentifiedButtonWidget.Builder identifier(Identifier id) {
            this.id = id;
            return this;
        }

        public IdentifiedButtonWidget build() {
            IdentifiedButtonWidget buttonWidget = new IdentifiedButtonWidget(this.x, this.y, this.width, this.height, this.id, this.onPress, this.narrationSupplier);
            buttonWidget.setTooltip(this.tooltip);
            return buttonWidget;
        }
    }
}
