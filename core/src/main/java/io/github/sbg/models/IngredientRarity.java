package io.github.sbg.models;

public enum IngredientRarity {
    COMMON(0),
    UNCOMMON(1),
    RARE(2),
    EPIC(3),
    LEGENDARY(4);

    private final int level;

    IngredientRarity(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public IngredientRarity next() {
        switch (this) {
            case COMMON:
                return UNCOMMON;
            case UNCOMMON:
                return RARE;
            case RARE:
                return EPIC;
            case EPIC:
                return LEGENDARY;
            case LEGENDARY:
            default:
                return LEGENDARY;
        }
    }

}

