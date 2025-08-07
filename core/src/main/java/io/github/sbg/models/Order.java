package io.github.sbg.models;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import io.github.sbg.systems.IngredientSystem;
import io.github.sbg.systems.PlayerDataSystem;
import io.github.sbg.ui.CircularTimer;

public class Order {
    final private List<Integer> requiredIngredients;
    private CircularTimer timer;
    private Texture characterTexture;
    private Group groupActor;

    public Order(List<Integer> ingredients,Texture characterTexturePath) {
        requiredIngredients=ingredients;
        this.characterTexture=characterTexturePath;
    }

    public void setGroupActor(Group groupActor) {
        this.groupActor = groupActor;
    }

    public void setTimer(CircularTimer timer) {
        this.timer = timer;
    }

    public List<Integer> getRequiredIngredients() {
        return requiredIngredients;
    }

    public Texture getCharacterTexture() {
        return characterTexture;
    }

    public CircularTimer getTimer() {
        return timer;
    }

    public Group getGroupActor() {
        return groupActor;
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
    public int calcRewardPoints(){
        int total=0;

        for(int ingredientID : requiredIngredients){
            Ingredient ingredient= IngredientSystem.getIngredient(ingredientID);
            IngredientRarity currentRarity= PlayerDataSystem.Instance.getIngredientRarity(ingredientID);
            total += MathUtils.round(ingredient.getBaseValue()*(currentRarity.getLevel()+1));
        }


        return total;
    }
}

