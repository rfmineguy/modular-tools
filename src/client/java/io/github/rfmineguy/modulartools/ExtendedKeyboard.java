package io.github.rfmineguy.modulartools;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class Keyboard extends net.minecraft.client.Keyboard {
    public boolean[] keystates = new boolean[1000];
    private MinecraftClient client;

    public Keyboard(MinecraftClient client) {
        super(client);
        this.client = client;
    }

    @Override
    public void onKey(long window, int key, int scancode, int action, int modifiers) {
        if (window == client.getWindow().getHandle()) {
            if (action == GLFW.GLFW_PRESS) keystates[key] = true;
            if (action == GLFW.GLFW_RELEASE) keystates[key] = false;
        }
        super.onKey(window, key, scancode, action, modifiers);
    }

    public boolean isKeyPressed(int code) {
        return true;
    }
}
