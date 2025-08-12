package io.github.sbg.models;

import com.badlogic.gdx.graphics.Color;

public enum IngredientRarity {
    COMMON(1f),
    UNCOMMON(1.25f),
    RARE(1.5f),
    EPIC(1.75f),
    LEGENDARY(2f);

    private final float multiplier;

    IngredientRarity(float multiplier) {
        this.multiplier = multiplier;
    }

    public float getMultiplier() {
        return multiplier;
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
                return COMMON;
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

