package io.github.sbg.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Order {
    final private List<Integer> requiredIngredients;
    private float remainingTime;
    private String characterTexturePath;

    public Order(List<Integer> ingredients,String characterTexturePath) {
        requiredIngredients=ingredients;
        this.characterTexturePath=characterTexturePath;
        remainingTime=30f;
        System.out.println("texture path: "+characterTexturePath);
    }
    public void update(float delta) {
        remainingTime -= delta;
    }

    public boolean isExpired() {
        return remainingTime <= 0;
    }
    public List<Integer> getRequiredIngredients() {
        return requiredIngredients;
    }

    public String getCharacterTexturePath() {
        return characterTexturePath;
    }

    public float getRemainingTime() {
        return remainingTime;
    }

    public boolean matches(List<Integer> playerBurger) {
        if (playerBurger.size() != requiredIngredients.size()) return false;
        for (int i = 0; i < requiredIngredients.size(); i++) {
            if (!playerBurger.get(i).equals(requiredIngredients.get(i))) {
                return false;
            }
        }
        return true;
    }
}

