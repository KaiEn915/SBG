package io.github.sbg.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.Batch;

import io.github.sbg.models.Order;
import io.github.sbg.systems.OrderSystem;

// Extend Actor so it can be added to a Stage or Group
public class CircularTimer extends Actor {
    private OrderSystem orderSystem;
    private Order order;
    private float duration;
    private float timeLeft;
    private float radius = 25;
    private ShapeRenderer shapeRenderer;

    public CircularTimer(float duration, Order order, OrderSystem orderSystem, ShapeRenderer shapeRenderer) {
        this.duration = duration;
        this.timeLeft = duration;
        this.order = order;
        this.orderSystem = orderSystem;
        this.shapeRenderer = shapeRenderer;

        // Set the size of the actor to match the timer
        this.setSize(radius * 2, radius * 2);
    }

    // Use the act() method for logic updates
    @Override
    public void act(float delta) {
        super.act(delta); // Important: call super's act method

        timeLeft -= delta;
        if (timeLeft < 0) {
            orderSystem.abort(order);
            timeLeft = 0;
        }
    }

    // Use the draw() method for rendering
    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.end(); // Stop the SpriteBatch to use ShapeRenderer

        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.setTransformMatrix(batch.getTransformMatrix());

        // Apply the Actor's position and scale
        float x = getX();
        float y = getY();
        float currentRadius = radius * getScaleX();
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.circle(x + currentRadius, y + currentRadius, currentRadius);

        shapeRenderer.setColor(Color.GRAY);
        float percentage = timeLeft / duration;
        float angle = 360 * percentage;
        shapeRenderer.arc(x + currentRadius, y + currentRadius, currentRadius, 90, -angle);
        shapeRenderer.end();

        batch.begin(); // Resume the SpriteBatch
    }


    public boolean isFinished() {
        return timeLeft <= 0;
    }
}
