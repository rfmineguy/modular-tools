package io.github.rfmineguy.modulartools.util;

import net.minecraft.util.math.Vec3d;

public class VecUtil {
    public static Vec3d Lerp3d(Vec3d a, Vec3d b, float t) {
        double x = a.x + (b.x - a.x) * t;
        double y = a.y + (b.y - a.y) * t;
        double z = a.z + (b.z - a.z) * t;
        return new Vec3d(x, y, z);
    }

    public static Vec3d DirectionBetween(Vec3d a, Vec3d b) {
        return a.subtract(b).normalize();
    }

    public static double Lerp1d(double x, double x1, float t) {
        return x + (x1 - x) * t;
    }
}
