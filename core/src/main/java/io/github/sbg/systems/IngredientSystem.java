package io.github.sbg.systems;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import io.github.sbg.models.Ingredient;
import io.github.sbg.models.IngredientRarity;

import java.util.HashMap;
import java.util.Map;

public class IngredientSystem {
    private final static Map<Integer, Ingredient> ingredients = new HashMap<>();
    private final AssetManager assetManager;


    public IngredientSystem(AssetManager assetManager) {
        this.assetManager = assetManager;
        loadIngredients();
    }

    private void loadIngredients() {
        addIngredient(0, "Bun Bottom", 5, "ingredients/egg.png");
        addIngredient(1, "Bun Top", 5, "ingredients/cheese.png");
        addIngredient(2, "Beef", 10, "ingredients/friedEggs.png");
        addIngredient(3, "Chicken", 10, "ingredients/friedEggs.png");
        addIngredient(4, "Fish", 10, "ingredients/fish.png");
    }

    private void addIngredient(int id, String name, float baseValue, String texturePath) {
        assetManager.load(texturePath, Texture.class);
        assetManager.finishLoadingAsset(texturePath);

        Texture texture = assetManager.get(texturePath, Texture.class);

        Ingredient ingredient = new Ingredient(id, name, baseValue, texture);
        ingredients.put(id, ingredient);
    }

    public static Ingredient getIngredient(int id) {
        return ingredients.get(id);
    }


    public Map<Integer, Ingredient> getAllIngredients() {
        return ingredients;
    }

    public void dispose() {
        for (Ingredient ingredient : ingredients.values()) {
            ingredient.getTexture().dispose();
        }
    }
}
