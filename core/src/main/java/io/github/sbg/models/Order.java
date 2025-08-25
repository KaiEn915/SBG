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

    public void setCharacterTexture(Texture characterTexture) {
        this.characterTexture = characterTexture;
    }

    public void setGroupActor(Group groupActor) {
        this.groupActor = groupActor;
    }

    public void setTimer(CircularTimer timer) {
        this.timer = timer;
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
    public float calcRewardPoints(){
        float total=0;
        for (int ingredientID:requiredIngredients){
            Ingredient ingredient=IngredientSystem.getIngredient(ingredientID);
            IngredientRarity rarity=PlayerDataSystem.Instance.getIngredientRarity(ingredientID);
            total+=ingredient.calcValueBasedOnRarity(rarity);
        }
        return total;
    }

}

