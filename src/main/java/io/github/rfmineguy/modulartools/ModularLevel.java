package io.github.rfmineguy.modulartools;

public enum ModularLevel {
    NONE,
    ZERO,
    ONE,
    TWO,
    THREE,
    FOUR,
    FIVE,
    SIX,
    SEVEN,
    EIGHT;

    ModularLevel() {}

    @Override
    public String toString() {
        return switch (this) {
            case NONE -> "None";
            case ZERO -> "Zero";
            case ONE -> "One";
            case TWO -> "Two";
            case THREE -> "Three";
            case FOUR -> "Four";
            case FIVE -> "Five";
            case SIX -> "Six";
            case SEVEN -> "Seven";
            case EIGHT -> "Eight";
            case null, default -> "default";
        };
    }
}

