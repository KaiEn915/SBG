package io.github.sbg.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

import java.util.*;
import java.util.Random;
import java.util.stream.Collectors;

import io.github.sbg.models.Ingredient;
import io.github.sbg.models.Order;
import io.github.sbg.screens.GameScreen;

public class OrderSystem {
    private final Random random = new Random();
    private final PlayerDataSystem playerDataSystem;
    private final IngredientSystem ingredientSystem;
    private final GameScreen gameScreen;
    private List<Order> pendingOrders=new ArrayList<>();
    private List<String> characterTexturePaths=new ArrayList<>();
    private float orderInterval=10,nextOrder=5;
    private final int MAXIMUM_ORDER_AMOUNT=5;

    public OrderSystem(PlayerDataSystem playerDataSystem, IngredientSystem ingredientSystem,GameScreen gameScreen) {
        this.playerDataSystem = playerDataSystem;
        this.ingredientSystem = ingredientSystem;
        this.gameScreen=gameScreen;
        loadCharacterTexturePaths();
    }
    public void loadCharacterTexturePaths() {
        FileHandle directory = Gdx.files.internal("assets/characters");
        if (directory.isDirectory()) {
            for (FileHandle entry : directory.list()) {
                characterTexturePaths.add("characters/"+entry.name());
            }
        }

    }



    public Order getRandomGeneratedOrder() {
        List<Integer> order = new ArrayList<>(); // order means the ingredients in the order (use ingredient id)
        // decide customer character texture
        String characterTexturePath=characterTexturePaths.get(random.nextInt(characterTexturePaths.size()));


        order.add(0); // 0 is bun bottom

        // generate burger middle layers randomly
        List<Integer> unlockedIngredients = playerDataSystem.getUnlockedIngredients()
            .stream()
            .filter(t -> t != 0 && t != 1)
            .toList();

        // layers amount to generate
        int layers = 2 + random.nextInt(4);
        for (int i = 0; i < layers && !unlockedIngredients.isEmpty(); i++) {
            // random.nextInt() index not start from 2 because id 0, 1 (bun top, bottom) is already filtered
            order.add(unlockedIngredients.get(random.nextInt(unlockedIngredients.size())));
        }

        order.add(1); // 1 is bun top


        System.out.println("New order generated!\n");
        System.out.println(new Json().prettyPrint(order));
        return new Order(order,characterTexturePath);
    }
    public void update(float delta) {
        for (Order order : pendingOrders) {
            order.update(delta);
        }

        pendingOrders.removeIf(Order::isExpired);

        nextOrder-=delta;
        if (pendingOrders.size() < MAXIMUM_ORDER_AMOUNT && nextOrder <= 0) {
            generateOrder();
            nextOrder = orderInterval;
        }
    }
    public void generateOrder(){
        Order generatedOrder=getRandomGeneratedOrder();
        pendingOrders.add(generatedOrder);
        gameScreen.spawnCustomer(generatedOrder);
    }


}

