package io.github.rfmineguy.modulartools.util;

public class CommonInputUtil implements InputUtil {
    @Override
    public boolean IsKeyPressed(int key) {
        return false;
    }

    @Override
    public boolean HasShiftDown() {
        return false;
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
