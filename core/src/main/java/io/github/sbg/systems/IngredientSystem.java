package io.github.sbg.systems;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import io.github.sbg.models.Ingredient;
import io.github.sbg.models.IngredientRarity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class IngredientSystem {
    private final static Map<Integer, Ingredient> ingredients = new HashMap<>();
    private final AssetManager assetManager;


    public IngredientSystem(AssetManager assetManager) {
        this.assetManager = assetManager;
        loadIngredients();
    }

    private void loadIngredients() {
        addIngredient(0, "Bun Bottom", 1,25, "ingredients/bunBottom.png");
        addIngredient(1, "Bun Top", 1,25, "ingredients/bunTop.png");
        addIngredient(2, "Baked Potato", 2,50, "ingredients/bakedPotato.png");
        addIngredient(3, "Beef", 3,75, "ingredients/beef.png");
        addIngredient(4, "Chicken", 4,100, "ingredients/chicken.png");
        addIngredient(5, "Cooked Cod", 5,125, "ingredients/cookedCod.png");
        addIngredient(6, "Cooked Mutton", 6,150, "ingredients/cookedMutton.png");
        addIngredient(7, "Cooked Porkchop", 7,175, "ingredients/cookedPorkchop.png");
        addIngredient(8, "Cooked Rabbit", 8,200, "ingredients/cookedRabbit.png");
        addIngredient(9, "Cooked Salmon", 9,225, "ingredients/cookedSalmon.png");
        addIngredient(10, "Fried Eggs", 10,250, "ingredients/friedEggs.png");
        addIngredient(11, "Pumpkin Pie", 12,275, "ingredients/pumpkinPie.png");
        addIngredient(12, "Golden Apple", 15,350, "ingredients/goldenApple.png");
    }

    private void addIngredient(int id, String name, float baseValue, float baseUpgradeCost,String texturePath) {
        assetManager.load(texturePath, Texture.class);
        assetManager.finishLoadingAsset(texturePath);

        Texture texture = assetManager.get(texturePath, Texture.class);

        Ingredient ingredient = new Ingredient(id, name, baseValue,baseUpgradeCost ,texture);
        ingredients.put(id, ingredient);
    }

    public static Ingredient getIngredient(int id) {
        return ingredients.get(id);
    }


    public static Set<Integer> getAllIngredients() {
        return ingredients.keySet();
    }

    public void dispose() {
        for (Ingredient ingredient : ingredients.values()) {
            ingredient.getTexture().dispose();
        }
    }
}
