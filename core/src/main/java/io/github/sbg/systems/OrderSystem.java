package io.github.sbg.systems;

import com.badlogic.gdx.utils.Json;

import java.util.*;
import java.util.Random;
import java.util.stream.Collectors;

import io.github.sbg.models.Ingredient;
import io.github.sbg.models.Order;

public class OrderSystem {
    private final Random random = new Random();
    private final PlayerDataSystem playerDataSystem;
    private final IngredientSystem ingredientSystem;
    private List<Order> pendingOrders=new ArrayList<>();
    private float orderInterval=10,nextOrder=0;
    private final int MAXIMUM_ORDER_AMOUNT=5;

    public OrderSystem(PlayerDataSystem playerDataSystem, IngredientSystem ingredientSystem) {
        this.playerDataSystem = playerDataSystem;
        this.ingredientSystem = ingredientSystem;
    }

    public Order generateRandomOrder() {
        List<Integer> order = new ArrayList<>(); // order means the ingredients in the order (use ingredient id)

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
        return new Order(order);
    }
    public void update(float delta) {
        for (Order order : pendingOrders) {
            order.update(delta);
        }

        pendingOrders.removeIf(Order::isExpired);

        nextOrder-=delta;
        if (pendingOrders.size() < MAXIMUM_ORDER_AMOUNT && nextOrder <= 0) {
            pendingOrders.add(generateRandomOrder());
            nextOrder = orderInterval;
        }
    }

}

