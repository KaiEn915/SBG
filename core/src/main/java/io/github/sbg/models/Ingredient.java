package io.github.sbg.models;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

public class Ingredient {
    private int id;
    private String name;
    private float baseValue;
    private float baseUpgradeCost;
    private Texture texture;


    public Ingredient(int id, String name, float baseValue, float baseUpgradeCost, Texture texture) {
        this.id=id;
        this.name = name;
        this.baseValue = baseValue;
        this.baseUpgradeCost=baseUpgradeCost;
        this.texture=texture;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public float getBaseUpgradeCost() {
        return baseUpgradeCost;
    }

    public float getBaseValue() {
        return baseValue;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setBaseUpgradeCost(float baseUpgradeCost) {
        this.baseUpgradeCost = baseUpgradeCost;
    }

    public void setBaseValue(float baseValue) {
        this.baseValue = baseValue;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public float calcUpgradeCost(IngredientRarity currentRarity) {
        return baseUpgradeCost*currentRarity.getMultiplier();
    }

    public float calcValueBasedOnRarity(IngredientRarity rarity) {
        return baseValue * rarity.getMultiplier();
    }

}
