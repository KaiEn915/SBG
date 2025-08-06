package io.github.sbg.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.sbg.MyGame;

public class MainMenuScreen extends ScreenAdapter {
    private final MyGame game;
    private Stage stage;
    private Skin skin;

    public MainMenuScreen(MyGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Use LibGDX built-in skin
        skin = new Skin(Gdx.files.internal("uiskin.json")); // make sure uiskin.atlas and uiskin.json are in assets

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // Buttons
        TextButton playButton = new TextButton("Game", skin);
        TextButton settingsButton = new TextButton("Settings", skin);
        TextButton quitButton = new TextButton("Quit", skin);

        table.add(playButton).fillX().uniformX().uniformY().pad(10).width(400).height(200);
        table.row();
        table.add(settingsButton).fillX().fillY().uniformX().uniformY().pad(10);
        table.row();
        table.add(quitButton).fillX().fillY().uniformX().uniformY().pad(10);

        // Listeners
        playButton.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                System.out.println("Start Game!");
                // game.setScreen(new GameScreen(game));
            }
            return false;
        });

        settingsButton.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                System.out.println("Go to Settings!");
                // game.setScreen(new SettingsScreen(game));
            }
            return false;
        });

        quitButton.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                Gdx.app.exit();
            }
            return false;
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
