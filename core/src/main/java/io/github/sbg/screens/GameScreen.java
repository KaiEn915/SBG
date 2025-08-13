package io.github.sbg.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.SnapshotArray;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.List;
import java.util.Set;

import io.github.sbg.MyGame;
import io.github.sbg.models.Ingredient;
import io.github.sbg.models.IngredientRarity;
import io.github.sbg.models.Order;
import io.github.sbg.systems.IngredientSystem;
import io.github.sbg.systems.OrderSystem;
import io.github.sbg.systems.PlayerDataSystem;

import io.github.sbg.ui.CircularTimer;

public class GameScreen implements Screen {
    private MyGame game;
    private Stage stage;
    private Skin skin;

    private OrderSystem orderSystem;
    private Stack rootStack;
    private Group customerGroup;
    private VerticalGroup playerBurgerGroup;
    private Random random = new Random();

    private ShapeRenderer shapeRenderer;
    private int currentDay;
    private PlayerDataSystem playerData;
    private boolean isGameStarted = false;

    private Label infoLabel;
    private float score;
    private float pointsEarned;
    private Label timerLabel;
    private float timer = 90;

    private Table topBar;
    private Table mainUiTable;
    private Table ingredientTable;
    private Table centerTable;
    private Table cabinet;
    private Table burgerTable;

    // These actors are now managed by adding/removing them from the burgerTable
    private TextButton startButton;
    private TextButton clearButton;
    private ScrollPane playerBurgerPane;
    private Container<Table> ingredientTableContainer;

    public GameScreen(MyGame game) {
        shapeRenderer = new ShapeRenderer();
        this.game = game;
        orderSystem = new OrderSystem( this, shapeRenderer);
    }

    @Override
    public void show() {
        isGameStarted = false;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        playerData = PlayerDataSystem.Instance;
        skin = game.skin;
        currentDay = PlayerDataSystem.Instance.getDay();

        stage.addActor(loadBackground());

        rootStack = new Stack();
        rootStack.setFillParent(true);
        stage.addActor(rootStack);

        loadTopBar();
        loadMainUiTable();
        setupBurgerActors(); // New method to create and configure the actors once
        updateUIForState();
    }

    public Image loadBackground() {
        Texture backgroundTexture = game.assetManager.get("ui/background.png", Texture.class);
        Image backgroundImage = new Image(backgroundTexture);
        backgroundImage.setFillParent(true);
        return backgroundImage;
    }

    public void loadTopBar() {
        topBar = new Table();
        TextButton pauseButton = new TextButton("Pause", skin);
        topBar.add(pauseButton).left().expandX().pad(10);
        timerLabel = new Label("Time Left: X", skin);
        topBar.add(timerLabel).center().expandX().pad(10);
        infoLabel = new Label("Day X | Score: X | Point: X", skin);
        topBar.add(infoLabel).right().expandX().pad(10);

        rootStack.add(topBar.top());
    }

    public void loadMainUiTable() {
        mainUiTable = new Table();
        mainUiTable.setFillParent(true);

        customerGroup = new Group();
        loadCenterTable();
        ingredientTableContainer = new Container<>();

        mainUiTable.add(customerGroup).expandX().fillX().row();
        mainUiTable.add(centerTable).expandX().fill().row();
        mainUiTable.add(ingredientTableContainer).expandX().fillX();
        rootStack.add(mainUiTable.bottom());
    }

    // NEW METHOD: Create the actors once so listeners are only set up one time
    private void setupBurgerActors() {
        playerBurgerGroup = new VerticalGroup();
        playerBurgerGroup.space(-120);

        playerBurgerPane = new ScrollPane(playerBurgerGroup, skin);
        playerBurgerPane.setScrollingDisabled(true, false);

        clearButton = new TextButton("Clear", skin);
        clearButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                orderSystem.clearPlayerBurger();
            }
        });

        startButton = new TextButton("Start", skin);
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                isGameStarted = true;
                timer = 90;
                pointsEarned = 0;
                score = 0;
                updateUIForState();
            }
        });
    }

    // CHANGED: This method no longer adds state-specific actors.
    // It only sets up the core structural tables.
    public void loadCenterTable() {
        centerTable = new Table();
        burgerTable = new Table();
        cabinet = new Table();

        ScrollPane cabinetPane = new ScrollPane(cabinet, skin);
        cabinetPane.setScrollingDisabled(false, true);

        Texture burgerTableTexture = game.assetManager.get("ui/burgerTable.png", Texture.class);
        TextureRegionDrawable burgerTableDrawable = new TextureRegionDrawable(new TextureRegion(burgerTableTexture));
        burgerTable.setBackground(burgerTableDrawable);

        centerTable.add(burgerTable).expandX().fillX().colspan(3);
        centerTable.add(cabinetPane).fillX().prefWidth(300).colspan(1);
    }

    // REFACTORED: Now clears and adds actors directly based on game state.
    private void updateUIForState() {
        // Clear all current actors from the dynamic containers
        burgerTable.clearChildren();
        customerGroup.clearChildren();

        boolean isPrepareStage = !isGameStarted;

        if (isPrepareStage) {
            // Prepare stage: show start button and unlock ingredients
            burgerTable.add(startButton).center().expand().row();
            ingredientTable = loadIngredientTable(true);
            customerGroup.setVisible(false); // No customers in prepare stage
        } else {
            // Game stage: show burger and clear button
            burgerTable.add(playerBurgerPane).width(300).padTop(10).padBottom(10).expandY().fillY().maxHeight(200);
            burgerTable.add(clearButton).center().padRight(100);

            ingredientTable = loadIngredientTable(false);
            customerGroup.setVisible(true); // Customers are visible during gameplay
        }

        ingredientTableContainer.setActor(ingredientTable);

        // Force the layout to update after adding/removing actors
        mainUiTable.invalidate();
    }

    public Table loadIngredientTable(boolean isPrepareStage) {
        Table newIngredientTable = new Table();
        int columns = 8;
        int count = 0;

        Collection<Integer> ingredientsToDisplay = isPrepareStage ?
            IngredientSystem.getAllIngredients() :
            playerData.getUnlockedIngredients();

        for (Integer ingredientID : ingredientsToDisplay) {
            Ingredient ingredient = IngredientSystem.getIngredient(ingredientID);
            Texture texture = ingredient.getTexture();
            IngredientRarity rarity = playerData.getIngredientRarity(ingredientID);

            Group ingredientGroup = new Group();
            float groupWidth = 225;
            float groupHeight = 125;
            float imageSize = groupHeight / 3;

            for (int i = 0; i < 15; i++) {
                Image img = new Image(texture);
                img.setSize(imageSize, imageSize);
                float x = MathUtils.random(0, groupWidth - imageSize);
                float y = MathUtils.random(0, groupHeight - imageSize);
                img.setPosition(x, y);
                ingredientGroup.addActor(img);
            }
            ingredientGroup.setSize(groupWidth, groupHeight);

            Container<Actor> backgroundContainer = new Container<>();
            Drawable background = skin.getDrawable("window");
            backgroundContainer.setBackground(background);
            backgroundContainer.setColor(rarity.getBorderColor());

            Stack stackedGroup = new Stack();
            stackedGroup.setSize(groupWidth, groupHeight);
            stackedGroup.addActor(backgroundContainer);
            stackedGroup.addActor(ingredientGroup);

            if (isPrepareStage) {
                float cost = ingredient.getUpgradeCost(rarity);
                boolean isMaxed = playerData.isIngredientMaxed(ingredientID);
                boolean isUnlocked = playerData.isIngredientUnlocked(ingredientID);

                TextButton button = new TextButton("", skin);
                button.setText(isMaxed ? "Maxed" : isUnlocked ? "Upgrade" : "Unlock");
                button.setDisabled(isMaxed || playerData.getGamePoints() < cost);

                Container<TextButton> buttonContainer = new Container<>(button);
                buttonContainer.size(groupWidth, 40);
                buttonContainer.align(Align.bottom);

                final int finalIngredientID = ingredientID;
                button.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if (button.isDisabled()) return;

                        if (isUnlocked) {
                            playerData.upgradeIngredient(finalIngredientID);
                        } else {
                            playerData.unlockIngredient(finalIngredientID);
                        }
                        playerData.removeGamePoints(cost);
                        updateUIForState();
                    }
                });
                stackedGroup.add(buttonContainer);
            } else {
                final int finalIngredientID = ingredientID;
                ingredientGroup.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        orderSystem.addIngredientToPlayerBurger(finalIngredientID);
                    }
                });
            }

            newIngredientTable.add(stackedGroup).size(groupWidth, groupHeight);
            count++;
            if (count % columns == 0) newIngredientTable.row();
        }
        return newIngredientTable;
    }

    public void addPointsEarned(float point) {
        pointsEarned += point;
    }

    public MyGame getGame() {
        return game;
    }

    public void updatePlayerBurgerGroup(List<Integer> ingredients) {
        playerBurgerGroup.clear();
        for (int i = ingredients.size() - 1; i >= 0; i--) {
            Ingredient ingredient = IngredientSystem.getIngredient(ingredients.get(i));
            Texture ingredientTexture = ingredient.getTexture();
            Image ingredientImage = new Image(ingredientTexture);
            ingredientImage.setSize(100, 50);
            playerBurgerGroup.addActor(ingredientImage);
        }
        playerBurgerGroup.invalidate();
        playerBurgerPane.scrollTo(0, playerBurgerGroup.getHeight(), 0, 0);
    }

    public void spawnCustomer(Order order) {
        float screenWidth = stage.getViewport().getWorldWidth();
        Set<Float> existingSpawnX = new HashSet<>();
        for (Actor actor : customerGroup.getChildren()) {
            existingSpawnX.add(actor.getX());
        }

        float minX = screenWidth * 0.25f;
        float maxX = screenWidth * 0.75f;
        float randomX;
        boolean validPosition;
        int times = 0;
        do {
            randomX = minX + random.nextFloat() * (maxX - minX);
            validPosition = true;
            for (float x : existingSpawnX) {
                if (Math.abs(randomX - x) < 100f) {
                    validPosition = false;
                    break;
                }
            }
            times++;
            if (times > 10) break;
        } while (!validPosition);

        Group singleCustomerGroup = new Group();
        Image customerImage = new Image(order.getCharacterTexture());
        customerImage.setSize(300, 300);
        customerImage.setPosition(0, 0);

        Texture chatBubbleTexture = game.assetManager.get("ui/chatBubble.png", Texture.class);
        Image chatBubbleImage = new Image(chatBubbleTexture);
        chatBubbleImage.setSize(150, 100);
        chatBubbleImage.setPosition(
            customerImage.getX() + customerImage.getWidth() - chatBubbleImage.getWidth() * 0.8f,
            customerImage.getY() + customerImage.getHeight() - 20
        );

        VerticalGroup burgerStackGroup = getBurgerStackGroup(order.getRequiredIngredients());
        burgerStackGroup.setPosition(
            chatBubbleImage.getX() + (chatBubbleImage.getWidth() - burgerStackGroup.getWidth()) / 2f,
            chatBubbleImage.getY() + (chatBubbleImage.getHeight() - burgerStackGroup.getHeight()) / 2f
        );

        CircularTimer timerActor = order.getTimer();
        timerActor.setPosition(
            customerImage.getX() + customerImage.getWidth() / 2f - timerActor.getWidth() / 2f,
            customerImage.getY() + customerImage.getHeight()
        );

        TextButton submitButton = new TextButton("Submit", skin);
        submitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                orderSystem.submitPlayerBurgerTo(order);
            }
        });
        float buttonWidth = 150;
        submitButton.setSize(buttonWidth, 50);
        submitButton.setPosition(customerImage.getWidth() / 2 - buttonWidth / 2, 0);

        singleCustomerGroup.setPosition(randomX, 0);

        order.setGroupActor(singleCustomerGroup);
        singleCustomerGroup.addActor(customerImage);
        singleCustomerGroup.addActor(submitButton);
        singleCustomerGroup.addActor(chatBubbleImage);
        singleCustomerGroup.addActor(burgerStackGroup);
        singleCustomerGroup.addActor(timerActor);
        customerGroup.addActor(singleCustomerGroup);
    }

    public VerticalGroup getBurgerStackGroup(List<Integer> burger) {
        float ingredientStackSize = 50;
        VerticalGroup burgerStackGroup = new VerticalGroup();
        burgerStackGroup.space(-ingredientStackSize / 1.5f);
        burgerStackGroup.reverse();
        for (int ingredientID : burger) {
            Ingredient ingredient = IngredientSystem.getIngredient(ingredientID);
            Texture ingredientTexture = ingredient.getTexture();
            Image ingredientImage = new Image(ingredientTexture);
            ingredientImage.setScaling(Scaling.stretch);
            Container<Image> container = new Container<>(ingredientImage);
            container.size(ingredientStackSize, ingredientStackSize);
            burgerStackGroup.addActor(container);
        }
        burgerStackGroup.pack();
        return burgerStackGroup;
    }

    public Group getCustomerGroup() {
        return customerGroup;
    }

    public void addScore(float score) {
        this.score += score;
    }

    public void halfScore() {
        score /= 2;
    }

    public Table getCabinet() {
        return cabinet;
    }

    public void moveBurgerToCabinet(List<Integer> burger) {
        VerticalGroup burgerGroup = getBurgerStackGroup(burger);
        cabinet.add(burgerGroup).size(100, 150);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
        infoLabel.setText("Day " + currentDay + " | Score: " + (int) score + " | Point: " + (int) PlayerDataSystem.Instance.getGamePoints());

        if (!isGameStarted) return;
        timer -= delta;
        if (timer <= 0) {
            gameOver();
            return;
        }
        orderSystem.update(delta);
        timerLabel.setText("Time Left: " + (int) Math.ceil(timer));
    }

    public void gameOver() {
        isGameStarted = false;
        stage.getRoot().clearChildren();
        stage.addActor(loadBackground());

        Image overlay = new Image(skin.newDrawable("white", new Color(0, 0, 0, 0.7f)));
        overlay.setFillParent(true);
        stage.addActor(overlay);

        Window gameOverWindow = new Window("",skin);
        gameOverWindow.pad(20);
        gameOverWindow.setModal(true);
        gameOverWindow.setMovable(false);

        Label gameOverLabel = new Label("Game Over", skin, "title");
        float highestScore = PlayerDataSystem.Instance.getHighestScore();
        if (score > highestScore) {
            PlayerDataSystem.Instance.setHighestScore(score);
            highestScore = score;
        }

        Label highestPointLabel = new Label("Highest Score: " + (int) highestScore, skin);
        Label moneyLabel = new Label("Total Points Earned: " + (int) pointsEarned, skin);
        Label scoreLabel = new Label("Score This Day: " + (int) score, skin);

        Table wrongBurgersTable = new Table();
        wrongBurgersTable.defaults().pad(5);
        SnapshotArray<Actor> actors = cabinet.getChildren();
        for (Actor actor : actors) {
            if (actor instanceof VerticalGroup) {
                wrongBurgersTable.add(actor).size(100, 150).row();
            }
        }

        ScrollPane wrongBurgersPane = new ScrollPane(wrongBurgersTable, skin);
        wrongBurgersPane.setFadeScrollBars(false);
        wrongBurgersPane.setScrollingDisabled(false, false);
        Label wrongLabel = new Label("Incorrect Burgers, they are now being donated to people who need it!", skin);

        TextButton backButton = new TextButton("Proceed to Next Day", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
                PlayerDataSystem.Instance.nextDay();
            }
        });

        gameOverWindow.add(gameOverLabel).center().padBottom(10).row();
        gameOverWindow.add(moneyLabel).left().padBottom(5).row();
        gameOverWindow.add(scoreLabel).left().padBottom(5).row();
        gameOverWindow.add(highestPointLabel).left().padBottom(20).row();
        gameOverWindow.add(wrongLabel).left().row();
        gameOverWindow.add(wrongBurgersPane).size(300, 200).row();
        gameOverWindow.add(backButton).padTop(20).center();

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
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        shapeRenderer.dispose();
    }
}
