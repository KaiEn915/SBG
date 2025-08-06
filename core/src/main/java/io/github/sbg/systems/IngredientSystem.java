package io.github.sbg.systems;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.sbg.models.Ingredient;
import io.github.sbg.models.IngredientRarity;

import java.util.HashMap;
import java.util.Map;

public class IngredientSystem {
    private final Map<String, Ingredient> ingredients = new HashMap<>();
    private final AssetManager assetManager;


    public IngredientSystem(AssetManager assetManager) {
        this.assetManager = assetManager;
        loadIngredients();
    }

    private void loadIngredients() {
        addIngredient(0,"Bun Bottom",5, "ingredients/bunBottom.png");
        addIngredient(1,"Bun Top",5, "ingredients/bunTop.png");
        addIngredient(2,"Tomato",10, "ingredients/tomato.png");
    }

    private void addIngredient(int id, String name, float baseValue, String texturePath) {
        assetManager.load(texturePath, Texture.class);
        assetManager.finishLoadingAsset(texturePath);

        Texture texture = assetManager.get(texturePath, Texture.class);

        Ingredient ingredient = new Ingredient(id,name, baseValue,texture);
        ingredients.put(name, ingredient);
    }

    public Ingredient getIngredient(String name) {
        return ingredients.get(name);
    }


    public Map<String, Ingredient> getAllIngredients() {
        return ingredients;
    }

    public void dispose() {
        for (Ingredient ingredient : ingredients.values()) {
            ingredient.getTexture().dispose();
        }
    }
}
