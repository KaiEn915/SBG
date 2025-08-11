package io.github.sbg.models;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

public class Ingredient {
    private int id;
    private String name;
    private float baseValue;
    private Texture texture;


    public Ingredient(int id, String name, float baseValue, Texture texture) {
        this.id=id;
        this.name = name;
        this.baseValue = baseValue;
        this.texture=texture;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public float getBaseValue() {
        return baseValue;
    }

    public Texture getTexture() {
        return texture;
    }

    public float getValueBasedOnRarity(IngredientRarity rarity) {
        return baseValue * rarity.getMultiplier();
    }

}
