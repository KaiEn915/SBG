package io.github.sbg;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import io.github.sbg.screens.MainMenuScreen;
import io.github.sbg.systems.IngredientSystem;
import io.github.sbg.systems.OrderSystem;
import io.github.sbg.systems.PlayerDataSystem;

public class MyGame extends Game {
    public IngredientSystem ingredientSystem;
    public PlayerDataSystem playerDataSystem;
    public AssetManager assetManager;
    public Skin skin;

    @Override
    public void create() {
        skin=new Skin(Gdx.files.internal("craftacular-ui.json"));

        // loading asset manager
        assetManager = new AssetManager();
            // ui textures
        loadAssetTextures("ui");
            // ingredient textures
//        loadAssetTextures("ingredients");
            // character textures
        loadAssetTextures("characters");

            //
        assetManager.finishLoading();
        //

        // load and initialize systems
        ingredientSystem=new IngredientSystem(assetManager);
        playerDataSystem=new PlayerDataSystem(); // player data will be loaded inside its constructor.

        this.setScreen(new MainMenuScreen(this));
    }
    public void loadAssetTextures(String assetDirectoryPath){
        FileHandle directory = Gdx.files.internal("assets/"+assetDirectoryPath);
        if (directory.isDirectory()) {
            for (FileHandle entry : directory.list()) {
                assetManager.load(assetDirectoryPath+"/"+entry.name(),Texture.class);
            }
        }
    }

    @Override
    public void dispose() {
        assetManager.dispose();
        getScreen().dispose();
    }
}
