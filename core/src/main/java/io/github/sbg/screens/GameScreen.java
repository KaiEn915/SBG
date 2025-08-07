package io.github.sbg.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.Collections;
import java.util.Random;
import java.util.List;
import java.util.Collection;


import io.github.sbg.MyGame;
import io.github.sbg.models.Ingredient;
import io.github.sbg.models.Order;
import io.github.sbg.systems.OrderSystem;

public class GameScreen implements Screen {
    private MyGame game;
    private Stage stage;
    private Skin skin;

    private OrderSystem orderSystem;
    private Table rootTable;
    private Group customerGroup;
    private VerticalGroup playerBurgerGroup;
    private Random random = new Random();

    public GameScreen(MyGame game) {
        this.game = game;
        orderSystem = new OrderSystem(game.playerDataSystem, game.ingredientSystem, this);
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = game.skin;

        rootTable = new Table();
        rootTable.setFillParent(true);

        customerGroup = new Group();

        // === Player Burger Group ===
        playerBurgerGroup = new VerticalGroup();
        playerBurgerGroup.space(-80);



        // ======= Top Bar =======
        TextButton pauseButton = new TextButton("Pause", skin);
        Label scoreLabel = new Label("Day 1 | Score: 0", skin);

        Table topBar = new Table();
        topBar.add(pauseButton).left().expandX().pad(10);
        topBar.add(scoreLabel).right().expandX().pad(10);

        // ======= Center Burger Table =======
        Texture burgerTableTexture = game.assetManager.get("ui/burgerTable.png", Texture.class);
        Table centerTable = new Table();
        centerTable.setBackground(new TextureRegionDrawable(new TextureRegion(burgerTableTexture)));
        centerTable.add(playerBurgerGroup).center().bottom().padBottom(20);
        centerTable.setHeight(300);

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
        rootTable.add(customerGroup).height(300).center().padBottom(-10).row();
        rootTable.add(centerTable).expandX().fillX().center().row();
        rootTable.add(ingredientTable).height(100);

        stage.addActor(backgroundImage);
        stage.addActor(rootTable);

        // ======= Test Ingredients =======
        orderSystem.addIngredientToPlayerBurger(3);
        orderSystem.addIngredientToPlayerBurger(2);
        orderSystem.addIngredientToPlayerBurger(4);
    }

    public void updatePlayerBurgerGroup(List<Integer> ingredients) {
        playerBurgerGroup.clear();
        Collections.reverse(ingredients);

        for (int id : ingredients) {
            Ingredient ingredient = game.ingredientSystem.getIngredient(id);
            Texture ingredientTexture = ingredient.getTexture();
            Image ingredientImage = new Image(ingredientTexture);
            ingredientImage.setSize(100, 50); // Optional
            playerBurgerGroup.addActor(ingredientImage);
        }

        // Force layout update
        playerBurgerGroup.invalidate();
        playerBurgerGroup.validate();
    }

    public void spawnCustomer(Order order) {
        Group customerGroup = new Group();

        Texture customerTexture = game.assetManager.get(order.getCharacterTexturePath(), Texture.class);
        Image customerImage = new Image(customerTexture);
        customerImage.setSize(300, 300);
        customerImage.setPosition(0, 0);

        Texture chatBubbleTexture = game.assetManager.get("ui/chatBubble.png", Texture.class);
        Image chatBubbleImage = new Image(chatBubbleTexture);
        chatBubbleImage.setSize(150, 100);
        chatBubbleImage.setPosition(
            customerImage.getX() + customerImage.getWidth() - chatBubbleImage.getWidth() * 0.8f,
            customerImage.getY() + customerImage.getHeight() - 20
        );

        Texture burgerOrderTexture = game.assetManager.get("ui/missingTexture.png", Texture.class);
        Image burgerOrderImage = new Image(burgerOrderTexture);
        burgerOrderImage.setSize(60, 60);
        burgerOrderImage.setPosition(
            chatBubbleImage.getX() + (chatBubbleImage.getWidth() - burgerOrderImage.getWidth()) / 2f,
            chatBubbleImage.getY() + (chatBubbleImage.getHeight() - burgerOrderImage.getHeight()) / 2f
        );

        customerGroup.addActor(customerImage);
        customerGroup.addActor(chatBubbleImage);
        customerGroup.addActor(burgerOrderImage);

        float screenWidth = stage.getViewport().getWorldWidth();
        float minX = -700;
        float maxX = screenWidth - 300 - customerImage.getWidth();
        float randomX = minX + random.nextFloat() * (maxX - minX);
        customerGroup.setPosition(randomX, 0);

        if (MathUtils.randomBoolean()) {
            customerGroup.setScaleX(-1f);
            customerGroup.setPosition(randomX + customerGroup.getWidth(), 0);
        }

        this.customerGroup.addActor(customerGroup);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
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
