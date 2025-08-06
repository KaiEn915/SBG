package io.github.sbg.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Json.Serializable;

import java.util.*;
import java.util.logging.FileHandler;

import io.github.sbg.models.Ingredient;
import io.github.sbg.models.IngredientRarity;

public class PlayerDataSystem {
    public static final PlayerDataSystem instance = new PlayerDataSystem();

    private Set<Integer> unlockedIngredients = new HashSet<>();
    private Map<Integer, IngredientRarity> ingredientRarities = new HashMap<>();
    private float gamePoints;

    public IngredientRarity getIngredientRarity(int ingredientID) {
        return ingredientRarities.getOrDefault(ingredientID, IngredientRarity.COMMON);
    }

    public float getGamePoints() {
        return gamePoints;
    }

    public void setGamePoints(int gamePoints) {
        this.gamePoints = gamePoints;
    }


    public void unlockIngredient(int id) {
        if (!unlockedIngredients.contains(id)){
            ingredientRarities.put(id,IngredientRarity.COMMON); // initially common
        }
        unlockedIngredients.add(id);
    }
    public void upgradeIngredientRarity(int id) {
        IngredientRarity currentRarity=ingredientRarities.get(id);
        if (currentRarity!=null){
            ingredientRarities.put(id,currentRarity.next());
        }
    }
    public void saveData() {

        Json json = new Json();

        unlockIngredient(1);
        unlockIngredient(2);

        FileHandle file = Gdx.files.local("playerData.json");
        file.writeString(json.prettyPrint(this), false);
    }

    public void loadData(){
        FileHandle file = Gdx.files.local("playerData.json");

        if (file.exists()) {
            Json json = new Json();
            PlayerDataSystem loaded = json.fromJson(PlayerDataSystem.class, file.readString());
            unlockedIngredients=loaded.unlockedIngredients;
            ingredientRarities=loaded.ingredientRarities;
            gamePoints= loaded.gamePoints;
        } else {
            System.out.println("No save file found.");
            createNewData();

        }
    }
    public void createNewData(){
        unlockedIngredients=new HashSet<>();
        ingredientRarities=new HashMap<>();
        unlockIngredient(0);
        unlockIngredient(1);
        gamePoints=0;
    }
}
