package io.github.rfmineguy.modulartools.modules;

import net.minecraft.util.math.Vec3i;

public class MiningSizeCategoryData implements CategoryData {
    int width, height, depth;

    public static final MiningSizeCategoryData THREE_BY_THREE = new MiningSizeCategoryData(3, 3, 1);
    public static final MiningSizeCategoryData FIVE_BY_FIVE = new MiningSizeCategoryData(5, 5, 1);
    public static final MiningSizeCategoryData TWENTY_ONE_BY_TWENTY_ONE = new MiningSizeCategoryData(21, 21, 1);

    public MiningSizeCategoryData(int width, int height, int depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    public Vec3i getSize() {
        return new Vec3i(width, height, depth);
    }
}
