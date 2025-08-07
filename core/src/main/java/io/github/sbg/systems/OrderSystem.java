package io.github.sbg.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Json;

import java.util.*;
import java.util.Random;
import java.util.stream.Collectors;

import io.github.sbg.models.Ingredient;
import io.github.sbg.models.Order;
import io.github.sbg.screens.GameScreen;
import io.github.sbg.ui.CircularTimer;

public class OrderSystem {
    private final Random random = new Random();
    private final PlayerDataSystem playerDataSystem;
    private final IngredientSystem ingredientSystem;
    private final GameScreen gameScreen;
    private List<Order> pendingOrders = new ArrayList<>();
    private List<Texture> characterTextures = new ArrayList<>();
    private float orderInterval = 2, nextOrder = 5;
    private final int MAXIMUM_ORDER_AMOUNT = 5;
    private List<Integer> playerBurger = new ArrayList<>();
    private ShapeRenderer shapeRenderer; // Add a ShapeRenderer instance

    public OrderSystem(PlayerDataSystem playerDataSystem, IngredientSystem ingredientSystem, GameScreen gameScreen, ShapeRenderer shapeRenderer) {
        this.playerDataSystem = playerDataSystem;
        this.ingredientSystem = ingredientSystem;
        this.gameScreen = gameScreen;
        this.shapeRenderer = shapeRenderer; // Initialize ShapeRenderer
        loadCharacterTextures();
    }

    public void loadCharacterTextures() {
        FileHandle directory = Gdx.files.internal("assets/characters");
        if (directory.isDirectory()) {
            for (FileHandle entry : directory.list()) {
                Texture entryTexture = gameScreen.getGame().assetManager.get("characters/" + entry.name());
                characterTextures.add(entryTexture);
            }
        }
    }

    public void addIngredientToPlayerBurger(int id) {
        playerBurger.add(id);
        gameScreen.updatePlayerBurgerGroup(playerBurger);
    }
    public void clearPlayerBurger(){
        playerBurger.clear();
        gameScreen.updatePlayerBurgerGroup(playerBurger);
    }

    public Order getRandomGeneratedOrder() {
        List<Integer> orderIngredients = new ArrayList<>(); // order means the ingredients in the order (use ingredient id)
        // decide customer character texture
        Texture characterTexture = characterTextures.get(random.nextInt(characterTextures.size()));

        orderIngredients.add(0); // 0 is bun bottom

        // generate burger middle layers randomly
        List<Integer> unlockedIngredients = playerDataSystem.getUnlockedIngredients()
            .stream()
            .filter(t -> t != 0 && t != 1)
            .toList();

        // layers amount to generate
        int layers = 2 + random.nextInt(4);
        for (int i = 0; i < layers && !unlockedIngredients.isEmpty(); i++) {
            // random.nextInt() index not start from 2 because id 0, 1 (bun top, bottom) is already filtered
            orderIngredients.add(unlockedIngredients.get(random.nextInt(unlockedIngredients.size())));
        }

        orderIngredients.add(1); // 1 is bun top

        Order order = new Order(orderIngredients, characterTexture);

        // Pass the shared ShapeRenderer instance here
        CircularTimer timer = new CircularTimer(30, order, this, shapeRenderer);
        order.setTimer(timer); // Assume a new setCircularTimer method in Order

        return order;
    }

    public void update(float delta) {
        nextOrder -= delta;

        if (pendingOrders.size() < MAXIMUM_ORDER_AMOUNT && nextOrder <= 0) {
            generateOrder();
            nextOrder = orderInterval;
        }
    }

    public void generateOrder() {
        Order generatedOrder = getRandomGeneratedOrder();
        pendingOrders.add(generatedOrder);
        gameScreen.spawnCustomer(generatedOrder);
    }

    public void abort(Order order) { // customer order is expired
        pendingOrders.remove(order);
        gameScreen.getCustomerGroup().removeActor(order.getGroupActor());
        // You might want to remove the timer actor from the stage here.
        // For example: gameScreen.removeActor(order.getCircularTimer());
    }

    public void submitPlayerBurgerTo(Order order){
        boolean isBurgerMatch= order.matches(playerBurger);
        if (isBurgerMatch){
            orderSuccess(order);
        }
        else{
            orderFail(order);
        }

        clearPlayerBurger();
    }
    public void orderSuccess(Order order){
        System.out.println("Order success!");
        gameScreen.getGame().playerDataSystem.addGamePoints(order.calcRewardPoints());
        abort(order);
    }
    public void orderFail(Order order){
        System.out.println("Order fail! Moved to container");
    }
}
