package io.github.rfmineguy.modulartools;

import io.github.rfmineguy.modulartools.util.InputUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

public class ClientInputUtil implements InputUtil {
    public ClientInputUtil() {
    }
    @Override
    public boolean IsKeyPressed(int key) {
        return net.minecraft.client.util.InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), key);
    }

    public boolean HasShiftDown() {
        return Screen.hasShiftDown();
    }

    @Override
    public boolean IsIncrementPressed() {
        return false;
    }

    @Override
    public boolean IsDecrementPressed() {
        return false;
    }
}
