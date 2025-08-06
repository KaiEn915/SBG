package io.github.sbg;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import io.github.sbg.screens.MainMenuScreen;
import io.github.sbg.systems.PlayerDataSystem;

public class MyGame extends Game {
    public AssetManager assetManager;
    public Skin skin;

    @Override
    public void create() {
        skin=new Skin(Gdx.files.internal("craftacular-ui.json"));
        PlayerDataSystem.instance.loadData();
        assetManager = new AssetManager();
        assetManager.load("burgerTable.png", Texture.class);

        assetManager.finishLoading();
        this.setScreen(new MainMenuScreen(this));
    }

    @Override
    public void dispose() {
        assetManager.dispose();
        getScreen().dispose();
    }
}
