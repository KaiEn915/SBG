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
    public void reduce(float duration){
        timeLeft-=duration;
    }

    // Use the act() method for logic updates
    @Override
    public void act(float delta) {
        super.act(delta); // Important: call super's act method

        timeLeft -= delta;
        if (timeLeft < 0) {
            orderSystem.orderExpire(order);
            timeLeft = 0;
        }
    }

    public float getTimeLeft() {
        return timeLeft;
    }

    public float getDuration() {
        return duration;
    }

    // Use the draw() method for rendering
    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.end(); // Stop the SpriteBatch to use ShapeRenderer

        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.setTransformMatrix(batch.getTransformMatrix());

        float x = getX();
        float y = getY();
        float currentRadius = radius * getScaleX();
        float centerX = x + currentRadius;
        float centerY = y + currentRadius;

        // Draw the full circle background in gray
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.GRAY);
        shapeRenderer.circle(centerX, centerY, currentRadius);
        shapeRenderer.end();

        // Draw the filled arc for the time left in green
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.GREEN);
        float percentage = timeLeft / duration;
        float angle = 360 * percentage;
        shapeRenderer.arc(centerX, centerY, currentRadius, 90, -angle);
        shapeRenderer.end();

        // Draw the outline in black to make the timer pop
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.circle(centerX, centerY, currentRadius);
        shapeRenderer.end();

        batch.begin(); // Resume the SpriteBatch
    }


    public boolean isFinished() {
        return timeLeft <= 0;
    }
}
