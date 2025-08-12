package io.github.sbg.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Cullable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.SnapshotArray;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.List;
import java.util.Collection;
import java.util.Vector;

import io.github.sbg.MyGame;
import io.github.sbg.models.Ingredient;
import io.github.sbg.models.Order;
import io.github.sbg.systems.IngredientSystem;
import io.github.sbg.systems.OrderSystem;
import io.github.sbg.systems.PlayerDataSystem;

import io.github.sbg.ui.CircularTimer; // Import the CircularTimer class

public class GameScreen implements Screen {
    private MyGame game;
    private Stage stage;
    private Skin skin;

    private OrderSystem orderSystem;
    private Table rootTable;
    private Group customerGroup;
    private VerticalGroup playerBurgerGroup;
    private Random random = new Random();

    private ShapeRenderer shapeRenderer;

    // UIs to update
    private Label infoLabel;
    private float score;
    private float pointsEarned; // points earned this day
    private Label timerLabel;

    private float timer=10;
    private Table cabinet;

    private int currentDay;

    public GameScreen(MyGame game) {
        shapeRenderer = new ShapeRenderer();
        this.game = game;
        orderSystem = new OrderSystem(game.playerDataSystem,this,shapeRenderer);

    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = game.skin;

        rootTable = new Table();
        rootTable.setFillParent(true);

        customerGroup = new Group();

        currentDay=PlayerDataSystem.Instance.getDay();

        // ======= Top Bar =======
        TextButton pauseButton = new TextButton("Pause", skin);

        Table topBar = new Table();
        topBar.add(pauseButton).left().expandX().pad(10);
        timerLabel=new Label("Time Left: X", skin);
        topBar.add(timerLabel).center().expandX().pad(10);
        infoLabel=new Label("Day X | Score: X | Point: X", skin);
        topBar.add(infoLabel).right().expandX().pad(10);

        // ======= Center Table =======
        Table centerTable = new Table();
        centerTable.setHeight(300);
        Table burgerTable=new Table();
        cabinet=new Table();
        playerBurgerGroup=new VerticalGroup();
        playerBurgerGroup.space(-120);


        ScrollPane playerBurgerPane=new ScrollPane(playerBurgerGroup,skin);
        playerBurgerPane.setScrollingDisabled(true, false);

        ScrollPane cabinetPane=new ScrollPane(cabinet,skin);
        cabinetPane.setScrollingDisabled(false, true);

        Texture burgerTableTexture = game.assetManager.get("ui/burgerTable.png", Texture.class);
        TextureRegionDrawable burgerTableDrawable =
            new TextureRegionDrawable(new TextureRegion(burgerTableTexture));

        burgerTable.setBackground(burgerTableDrawable);
        burgerTable.add(playerBurgerPane).width(300).height(300).center();

        centerTable.add(burgerTable).expandX().fillX().colspan(3);
        centerTable.add(cabinetPane).fillX().prefWidth(300).colspan(1);

        TextButton clearButton=new TextButton("Clear",skin);
        clearButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                orderSystem.clearPlayerBurger();
            }
        });

        burgerTable.add(clearButton).center().padRight(100);

        // ======= Background =======
        Texture backgroundTexture = game.assetManager.get("ui/background.png", Texture.class);
        Image backgroundImage = new Image(backgroundTexture);
        backgroundImage.setFillParent(true);

        // ======= Bottom Ingredients =======
        Table ingredientTable = new Table();
        int columns = 5;
        int count = 0;

        for (Integer ingredientID : game.playerDataSystem.getUnlockedIngredients()) {
            Ingredient ingredient = IngredientSystem.getIngredient(ingredientID);
            Texture texture = ingredient.getTexture();

            Group ingredientGroup = new Group();
            float groupSize = 175;
            float imageSize = groupSize/2;

            // Add 5 images randomly
            for (int i = 0; i < 15; i++) {
                Image img = new Image(texture);
                img.setSize(imageSize, imageSize);
                float x = MathUtils.random(0, groupSize - imageSize);
                float y = MathUtils.random(0, groupSize - imageSize);
                img.setPosition(x, y);
                ingredientGroup.addActor(img);
            }
            ingredientGroup.setSize(groupSize, groupSize);

            // Make group clickable
            final int finalIngredientID = ingredientID;
            ingredientGroup.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    orderSystem.addIngredientToPlayerBurger(finalIngredientID);
                }
            });

            // Container with background and border
            Container<Actor> backgroundContainer = new Container<>();
            Drawable background=skin.getDrawable("window");
            backgroundContainer.setBackground(background);
            backgroundContainer.setColor(PlayerDataSystem.Instance.getIngredientRarity(ingredientID).getBorderColor());

            // Stack: background below, group on top
            Stack stackedGroup = new Stack();
            stackedGroup.setSize(backgroundContainer.getWidth(), backgroundContainer.getHeight());
            stackedGroup.addActor(backgroundContainer);
            stackedGroup.addActor(ingredientGroup);

            // Add to table
            ingredientTable.add(stackedGroup).size(groupSize,groupSize).pad(10);
            count++;
            if (count % columns == 0) ingredientTable.row();
        }




        // ======= Compose Layout =======
        rootTable.add(topBar).height(100).expandX().fillX().colspan(4).row();
        rootTable.add(customerGroup).height(300).center().padBottom(-10).row();
        rootTable.add(centerTable).height(300).expandX().fillX().center().row();
        centerTable.debug();
        rootTable.add(ingredientTable).bottom();

        stage.addActor(backgroundImage);
        stage.addActor(rootTable);

    }

    public void addPointsEarned(float point){
        pointsEarned+=point;
    }
    public MyGame getGame() {
        return game;
    }

    public void updatePlayerBurgerGroup(List<Integer> ingredients) {
        playerBurgerGroup.clear();

        for (int i=ingredients.size()-1;i>=0;i--) {
            Ingredient ingredient = IngredientSystem.getIngredient(ingredients.get(i));
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
        Group singleCustomerGroup = new Group();

        //
        Image customerImage = new Image(order.getCharacterTexture());
        customerImage.setSize(300, 300);
        customerImage.setPosition(0, 0);

        //
        Texture chatBubbleTexture = game.assetManager.get("ui/chatBubble.png", Texture.class);
        Image chatBubbleImage = new Image(chatBubbleTexture);
        chatBubbleImage.setSize(150, 100);
        chatBubbleImage.setPosition(
            customerImage.getX() + customerImage.getWidth() - chatBubbleImage.getWidth() * 0.8f,
            customerImage.getY() + customerImage.getHeight() - 20
        );

        // burger stack group image
        VerticalGroup burgerStackGroup=getPlayerBurgerGroup(order.getRequiredIngredients());
        burgerStackGroup.setPosition(
            chatBubbleImage.getX() + (chatBubbleImage.getWidth() - burgerStackGroup.getWidth()) / 2f,
            chatBubbleImage.getY() + (chatBubbleImage.getHeight() - burgerStackGroup.getHeight()) / 2f
        );

        //
        CircularTimer timerActor = order.getTimer();
        timerActor.setPosition(
            customerImage.getX() + customerImage.getWidth() / 2f - timerActor.getWidth() / 2f,
            customerImage.getY() + customerImage.getHeight() + 100
        );

        //
        TextButton submitButton=new TextButton("Submit",skin);
        submitButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                orderSystem.submitPlayerBurgerTo(order);
            }
        });
        submitButton.setSize(150,50);
        submitButton.setPosition(0,customerImage.getHeight());


        float imageWidth = customerImage.getWidth();
        float screenWidth = stage.getViewport().getWorldWidth();

        float padding = 200;
        float minX = -screenWidth / 2 + padding;
        float maxX = screenWidth / 2 - padding - imageWidth;

        float randomX = minX + random.nextFloat() * (maxX - minX);
        singleCustomerGroup.setPosition(randomX, 0);

        order.setGroupActor(singleCustomerGroup);

        singleCustomerGroup.addActor(submitButton);
        singleCustomerGroup.addActor(customerImage);
        singleCustomerGroup.addActor(chatBubbleImage);
        singleCustomerGroup.addActor(burgerStackGroup);
        singleCustomerGroup.addActor(timerActor);
        customerGroup.addActor(singleCustomerGroup);
    }

    public VerticalGroup getPlayerBurgerGroup(List<Integer> burger){
        float ingredientStackSize=50;
        VerticalGroup burgerStackGroup=new VerticalGroup();
        burgerStackGroup.space(-ingredientStackSize/1.5f);
        burgerStackGroup.reverse();
        for(int ingredientID:burger){
            Ingredient ingredient= IngredientSystem.getIngredient(ingredientID);
            Texture ingredientTexture = ingredient.getTexture();
            Image ingredientImage = new Image(ingredientTexture);
            ingredientImage.setScaling(Scaling.stretch); // makes it scale
            Container<Image> container = new Container<>(ingredientImage);
            container.size(ingredientStackSize, ingredientStackSize); // set desired size here
            burgerStackGroup.addActor(container);
        }
        burgerStackGroup.pack();
        return burgerStackGroup;
    }


    public Group getCustomerGroup() {
        return customerGroup;
    }


    public void addScore(float score){
        this.score+=score;
    }
    public void halfScore(){
        score/=2;
    }

    public Table getCabinet() {
        return cabinet;
    }
    public void moveBurgerToCabinet(List<Integer> burger){
        VerticalGroup playerBurgerGroup=getPlayerBurgerGroup(burger);
        cabinet.add(playerBurgerGroup);
    }
    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
        timer-=delta;
        if (timer<=0){
            gameOver();
            return;
        }


        orderSystem.update(delta);
        infoLabel.setText("Day "+currentDay+" | Score: "+score+" | Point: "+PlayerDataSystem.Instance.getGamePoints());
        timerLabel.setText("Time Left: "+(int)Math.ceil(timer));
        // The OrderSystem's update method should now handle updating the CircularTimer.
        // The render logic is handled by the Stage's draw() method.
    }

    public void gameOver() {
        // Pause the game
        stage.getRoot().clearChildren();
        Gdx.input.setInputProcessor(stage);
        // Create a semi-transparent overlay
        Image overlay = new Image(skin.newDrawable("white", new Color(0, 0, 0, 0.7f)));
        overlay.setFillParent(true);
        stage.addActor(overlay);

        // Main game over window
        Window gameOverWindow = new Window("",skin);
        gameOverWindow.pad(20);
        gameOverWindow.setModal(true);


        Label gameOverLabel = new Label("Game Over", skin);

        // highest point
        float highestScore=PlayerDataSystem.Instance.getHighestScore();
        if (score>highestScore){
            PlayerDataSystem.Instance.setHighestScore(score);
            highestScore=score;
        }

        Label highestPointLabel=new Label("Highest Score: "+highestScore,skin);

        // Total money
        Label moneyLabel = new Label("Total Points Earned: " + pointsEarned, skin);

        // Score earned this day
        Label scoreLabel = new Label("Score This Day: " + score, skin);

        // Wrong burgers from cabinet
        Table wrongBurgersTable = new Table();
        wrongBurgersTable.defaults().pad(5);
        for (Actor burgerGroup : cabinet.getChildren()) {
            wrongBurgersTable.add(getPlayerBurgerGroup(Arrays.asList(0, 2, 1))).prefSize(10).row();

        }
        ScrollPane wrongBurgersPane = new ScrollPane(wrongBurgersTable, skin);
        wrongBurgersPane.setFadeScrollBars(false);
        wrongBurgersPane.setScrollingDisabled(false, true);
        wrongBurgersPane.pack();
        Label wrongLabel = new Label("Wrong Burgers:", skin);

        // Back to main menu button
        TextButton backButton = new TextButton("Back to Main Menu, Proceed to the next day.", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game)); // Replace with your main menu screen class
                PlayerDataSystem.Instance.nextDay();
            }
        });

        // Add everything to window
        gameOverWindow.add(gameOverLabel).center().row();
        gameOverWindow.add(moneyLabel).left().row();
        gameOverWindow.add(scoreLabel).left().row();
        gameOverWindow.add(highestPointLabel).left().row();


        gameOverWindow.add(wrongLabel).left().row();
        gameOverWindow.add(wrongBurgersPane).size(300, 200).row();
        gameOverWindow.add(backButton).padTop(10).center();

        gameOverWindow.pack();
        gameOverWindow.setPosition(
            (stage.getWidth() - gameOverWindow.getWidth()) / 2f,
            (stage.getHeight() - gameOverWindow.getHeight()) / 2f
        );

        stage.addActor(gameOverWindow);


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
        shapeRenderer.dispose(); // Dispose the ShapeRenderer
    }
}
