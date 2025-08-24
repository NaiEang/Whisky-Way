package io.naieang.whiskyway;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Car {
    private Texture currentTexture; // The texture we are currently drawing

    public Vector2 position;
    private float speed = 100f;

    float carHeight = 32f;
    float carWidth = 32f;

    // Pathfinding
    private Polyline path;
    private int currentPathNode = 0;
    private Vector2 targetPosition;
    private Rectangle collisionRect;

    public Car(float startX, float startY, PolylineMapObject pathObject, float mapHeight) {

        currentTexture = AssetManager.carDown;
        this.path = pathObject.getPolyline();


        this.position = new Vector2(startX, startY - carHeight);

        this.targetPosition = new Vector2();
        setNextTarget();
        collisionRect = new Rectangle(position.x, position.y, carWidth, carHeight);
    }

    private void setNextTarget() {
        currentPathNode++;
        if (currentPathNode >= path.getVertices().length / 2) {
            currentPathNode = 0;
        }
        float targetX = path.getX() + path.getVertices()[currentPathNode * 2];
        float targetY = path.getY() + path.getVertices()[currentPathNode * 2 + 1];
        targetPosition.set(targetX, targetY);

        // --- NEW: Determine which texture to use based on direction ---
        Vector2 direction = targetPosition.cpy().sub(position);
        if (Math.abs(direction.x) > Math.abs(direction.y)) { // Moving more horizontally
            if (direction.x > 0) {
                currentTexture = AssetManager.carRight;
            } else {
                currentTexture = AssetManager.carLeft;
            }
        } else { // Moving more vertically
            if (direction.y > 0) {
                currentTexture = AssetManager.carUp;
            } else {
                currentTexture = AssetManager.carDown;
            }
        }
    }

    public void update(float deltaTime) {
        Vector2 moveDirection = targetPosition.cpy().sub(position).nor();
        position.add(moveDirection.scl(speed * deltaTime));

        if (position.dst(targetPosition) < 2.0f) {
            setNextTarget();
        }
        collisionRect.setPosition(position.x, position.y);
    }
    public Rectangle getCollisionRect() {
        return collisionRect;
    }

    public void render(SpriteBatch batch) {
        // Draw the current directional texture
        batch.draw(currentTexture, position.x, position.y, carWidth, carHeight);
    }

    public void dispose() {
    }
}
