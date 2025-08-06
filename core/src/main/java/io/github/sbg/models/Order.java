package io.github.sbg.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Order {
    final private List<Integer> requiredIngredients;
    private float remainingTime;

    public Order(List<Integer> ingredients) {
        requiredIngredients=ingredients;
        remainingTime=30f;
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

