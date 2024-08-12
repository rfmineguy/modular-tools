package io.github.rfmineguy.modulartools;

import net.minecraft.item.ItemStack;

import java.util.Comparator;

public enum ModularLevel {
    ERROR(-2),
    NONE(-1),
    ZERO(0),
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8);

    public final int level;
    ModularLevel(int level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return switch (level) {
            case -2 -> "Error";
            case -1 -> "None";
            case 0 -> "Zero";
            case 1 -> "One";
            case 2 -> "Two";
            case 3 -> "Three";
            case 4 -> "Four";
            case 5 -> "Five";
            case 6 -> "Six";
            case 7 -> "Seven";
            case 8 -> "Eight";
            default -> "Invalid";
        };
    }

    public ItemStack getLevelStack() {
        return switch (this) {
            case ERROR -> ModRegistration.ModBlocks.ERROR_BLOCK.asItem().getDefaultStack();
            case ONE   -> ModRegistration.ModBlocks.LEVEL1_BLOCK.asItem().getDefaultStack();
            case TWO   -> ModRegistration.ModBlocks.LEVEL2_BLOCK.asItem().getDefaultStack();
            case THREE -> ModRegistration.ModBlocks.LEVEL3_BLOCK.asItem().getDefaultStack();
            case FOUR  -> ModRegistration.ModBlocks.LEVEL4_BLOCK.asItem().getDefaultStack();
            case FIVE  -> ModRegistration.ModBlocks.LEVEL5_BLOCK.asItem().getDefaultStack();
            case SIX   -> ModRegistration.ModBlocks.LEVEL6_BLOCK.asItem().getDefaultStack();
            case SEVEN -> ModRegistration.ModBlocks.LEVEL7_BLOCK.asItem().getDefaultStack();
            case EIGHT -> ModRegistration.ModBlocks.LEVEL8_BLOCK.asItem().getDefaultStack();
            default -> ItemStack.EMPTY;
        };
    }

    public ModularLevel previous() {
        if (this == ModularLevel.NONE) return this;
        if (this == ModularLevel.ZERO) return ModularLevel.NONE;
        return ModularLevel.values()[level - 1];
    }

    public static final Comparator<ModularLevel> comparator = Comparator.comparingInt(o -> o.level);
}

