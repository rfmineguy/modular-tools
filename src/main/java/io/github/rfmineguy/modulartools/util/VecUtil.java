package io.github.rfmineguy.modulartools.util;

import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class LerpUtil {
    public static Vec3d Lerp3d(Vec3d a, Vec3d b, float t) {
        double x = a.x + (b.x - a.x) * t;
        double y = a.y + (b.y - a.y) * t;
        double z = a.z + (b.z - a.z) * t;
        return new Vec3d(x, y, y);
    }
}
