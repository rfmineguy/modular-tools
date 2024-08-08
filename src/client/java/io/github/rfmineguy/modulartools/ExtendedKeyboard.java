package io.github.rfmineguy.modulartools;

import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class ExtendedKeyboard extends Keyboard {
    public boolean[] keystatePrev = new boolean[1000];
    public boolean[] keystates = new boolean[1000];
    private final MinecraftClient client;

    public ExtendedKeyboard(MinecraftClient client) {
        super(client);
        this.client = client;
    }

    @Override
    public void onKey(long window, int key, int scancode, int action, int modifiers) {
        System.out.println("onKey");
        keystatePrev = keystates.clone();
        if (window == client.getWindow().getHandle()) {
            if (action == GLFW.GLFW_PRESS) {
                System.out.println("Pressed " + key);
                keystates[key] = true;
            }
            if (action == GLFW.GLFW_RELEASE) {
                System.out.println("Released " + key);
                keystates[key] = false;
            }
        }
        // super.onKey(window, key, scancode, action, modifiers);
    }

    public boolean isKeyPressed(int code) {
        return keystates[code] && !keystatePrev[code];
    }
}
