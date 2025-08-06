package io.github.sbg.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import javax.swing.GroupLayout;

import io.github.sbg.MyGame;
import io.github.sbg.systems.IngredientSystem;
import io.github.sbg.systems.OrderSystem;

public class GameScreen implements Screen {
    private MyGame game;
    private Stage stage;
    private Skin skin;

    private OrderSystem orderSystem;

    public GameScreen(MyGame game) {
        this.game = game;
        orderSystem = new OrderSystem(game.playerDataSystem, game.ingredientSystem);
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = game.skin; // Or VisUI skin

        Table rootTable = new Table();
        rootTable.setFillParent(true);

        // ======= Top Bar =======
        TextButton pauseButton = new TextButton("Pause", skin);
        Label scoreLabel = new Label("Day 1 | Score: 0", skin);

        Table topBar = new Table();
        topBar.add(pauseButton).left().expandX().pad(10);
        topBar.add(scoreLabel).right().expandX().pad(10);

        // === Customer Group ===
        Group customerGroup = new Group();

        // --- Customer Image ---
        Texture customerTexture = game.assetManager.get("characters/enderman.png", Texture.class);
        Image customerImage = new Image(customerTexture);
        customerImage.setSize(300, 300); // Fixed size
        customerImage.setPosition(0, 0); // Base reference point

        // --- Chat Bubble Background ---
        Texture chatBubbleTexture = game.assetManager.get("ui/chatBubble.png", Texture.class);
        Image chatBubbleImage = new Image(chatBubbleTexture);
        chatBubbleImage.setSize(150, 100); // Smaller and cleaner
        // Position above the top-right corner of customerImage
        chatBubbleImage.setPosition(
            customerImage.getX() + customerImage.getWidth() - chatBubbleImage.getWidth() * 0.8f,
            customerImage.getY() + customerImage.getHeight() - 20
        );

        // --- Burger Image inside the chat bubble ---
        Texture burgerOrderTexture = game.assetManager.get("ui/missingTexture.png", Texture.class); // Representing burger
        Image burgerOrderImage = new Image(burgerOrderTexture);
        burgerOrderImage.setSize(60, 60); // Make it fit inside chat bubble
        // Center inside the chat bubble
        burgerOrderImage.setPosition(
            chatBubbleImage.getX() + (chatBubbleImage.getWidth() - burgerOrderImage.getWidth()) / 2f,
            chatBubbleImage.getY() + (chatBubbleImage.getHeight() - burgerOrderImage.getHeight()) / 2f
        );

        // === Add all to group ===
        customerGroup.addActor(customerImage);
        customerGroup.addActor(chatBubbleImage);
        customerGroup.addActor(burgerOrderImage);

        // (Optional) Place the entire group on screen
        customerGroup.setPosition(500, 100); // Example: place near bottom-middle
        stage.addActor(customerGroup);

        // ======= Center Burger Table =======
        Texture burgerTableTexture = game.assetManager.get("ui/burgerTable.png", Texture.class);
        Image burgerTableImage = new Image(burgerTableTexture);

        // ======= Background =======
        Texture backgroundTexture = game.assetManager.get("ui/background.png", Texture.class);
        Image backgroundImage = new Image(backgroundTexture);
        backgroundImage.setFillParent(true);

        // ======= Bottom Ingredients =======
        Table ingredientTable = new Table();
        String[] ingredientNames = {"Lettuce", "Tomato", "Patty", "Cheese", "Bun"};

        for (String name : ingredientNames) {
            TextButton ingredientBtn = new TextButton(name, skin);
            ingredientTable.add(ingredientBtn).pad(5);
        }

        // ======= Compose Layout =======
        rootTable.top().pad(10);
        rootTable.add(topBar).height(200).expandX().fillX().row();
        rootTable.add(customerGroup).height(300).center().padBottom(10).row();
        rootTable.add(burgerTableImage).expand().fill().center().row();
        rootTable.add(ingredientTable).bottom().padBottom(20);
        stage.addActor(backgroundImage);
        stage.addActor(rootTable);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();

        //
        orderSystem.update(delta);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
