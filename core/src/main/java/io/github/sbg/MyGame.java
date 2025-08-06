package io.github.sbg;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import io.github.sbg.screens.MainMenuScreen;
import io.github.sbg.systems.PlayerDataSystem;

public class MyGame extends Game {
    public AssetManager assetManager;

    @Override
    public void create() {
        PlayerDataSystem.instance.loadData();
        assetManager = new AssetManager();
        this.setScreen(new MainMenuScreen(this));
    }

    @Override
    public void dispose() {
        assetManager.dispose();
        getScreen().dispose();
    }
}
