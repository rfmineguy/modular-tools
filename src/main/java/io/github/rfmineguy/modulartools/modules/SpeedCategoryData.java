package io.github.rfmineguy.modulartools.modules;

public class SpeedCategoryData implements CategoryData {
    float speedMultiplier;

    public static final SpeedCategoryData ONE = new SpeedCategoryData(1.3f);
    public static final SpeedCategoryData TWO = new SpeedCategoryData(2.0f);

    public SpeedCategoryData(float speedMultiplier) {
        this.speedMultiplier = speedMultiplier;
    }
}
