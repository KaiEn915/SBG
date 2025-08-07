package io.github.sbg.models;

import com.badlogic.gdx.graphics.Color;

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
    public Color getBorderColor(){
       switch (this) {
            case COMMON:
                return Color.WHITE;
            case UNCOMMON:
                return Color.GREEN;
            case RARE:
                return Color.BLUE;
            case EPIC:
                return Color.PURPLE;
            case LEGENDARY:
                return Color.YELLOW;
            default:
                return Color.WHITE;
        }
    }
}

