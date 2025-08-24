package io.naieang.whiskyway;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.bullet.collision._btMprSimplex_t;
import org.w3c.dom.css.Rect;

import javax.swing.*;

public class Dog {
    public Rectangle getCollisionRect;
    // --- NEW: Animation just like the player ---
    private Animation<TextureRegion> walkAnimation;
    private float stateTime;

    private float dogSpriteWidth = 32f;
    private float dogSpriteHeight = 32f;

    public Vector2 position;
    private float speed = 50f;

    // AI State
    private Rectangle movementZone;
    private Vector2 targetPosition;
    private float idleTimer = 0f;
    private boolean isMoving = false;
    private Rectangle collisionRect;

    // --- NEW: Collision Variables ---
    private TiledMapTileLayer collisionLayerBuildings;
    private TiledMapTileLayer collisionLayerWater;
    private TiledMapTileLayer collisionLayerFence;
    private TiledMapTileLayer collisionLayerPlant;

    private float tileWidth, tileHeight;
    // We'll give the dog a small collision box like the player
    private float collisionRectXOffset = 4f;
    private float collisionRectYOffset = 2f;
    private float collisionRectWidth = 8f;
    private float collisionRectHeight = 8f;

    public Dog(float startX, float startY, RectangleMapObject zoneObject, float mapHeight, TiledMapTileLayer buildingsLayer, TiledMapTileLayer waterLayer, TiledMapTileLayer fenceLayer, TiledMapTileLayer plantLayer) {
        position = new Vector2(startX, startY - dogSpriteHeight);
        targetPosition = new Vector2(startX, startY - dogSpriteHeight);

        // --- NEW: Animation Setup ---
        // We'll use the front-facing animation for the dog
        Texture dogFront1 = new Texture("NPC/dog_front.png");
        Texture dogFront2 = new Texture("NPC/dog_front1.png");
        walkAnimation = new Animation<>(0.3f, new TextureRegion(dogFront1), new TextureRegion(dogFront2));
        stateTime = 0f;
        float dogHeight = 16f;
        position = new Vector2(startX, startY - dogHeight);
        targetPosition = new Vector2(startX, startY - dogHeight);

        Rectangle tiledRect = zoneObject.getRectangle();
        float correctedZoneY = tiledRect.y - tiledRect.height;
        this.movementZone = new Rectangle(tiledRect.x, correctedZoneY, tiledRect.width, tiledRect.height);
        collisionRect = new Rectangle(position.x, position.y, dogSpriteWidth, dogSpriteHeight);

        this.collisionLayerBuildings = buildingsLayer;
        this.collisionLayerWater = waterLayer;
        this.collisionLayerFence = fenceLayer;
        this.collisionLayerPlant = plantLayer;
        this.tileWidth = buildingsLayer.getTileWidth();
        this.tileHeight = buildingsLayer.getTileHeight();


    }

    public void update(float deltaTime) {
        idleTimer -= deltaTime;

        if (position.dst(targetPosition) < 2.0f && idleTimer <= 0) {
            pickNewTarget();
        }

        if (position.dst(targetPosition) > 2.0f) {
            float oldX = position.x;
            float oldY = position.y;

            Vector2 direction = targetPosition.cpy().sub(position).nor();
            position.add(direction.scl(speed * deltaTime));
            isMoving = true;
            if(isCellBlocked(position.x, position.y)){
                position.set(oldX, oldY);
            }
        } else {
            isMoving = false;
        }

        if (isMoving) {
            stateTime += deltaTime;
        }
        collisionRect.setPosition(position.x, position.y);
    }

    private void pickNewTarget() {
        float newX = MathUtils.random(movementZone.x, movementZone.x + movementZone.width);
        float newY = MathUtils.random(movementZone.y, movementZone.y + movementZone.height);
        targetPosition.set(newX, newY);
        idleTimer = MathUtils.random(5.0f, 8.0f);
    }
    private boolean isCellBlocked(float dogX, float dogY) {
        float rectX = dogX + collisionRectXOffset;
        float rectY = dogY + collisionRectYOffset;

        boolean bottomLeft = isTileSolid(rectX, rectY);
        boolean bottomRight = isTileSolid(rectX + collisionRectWidth, rectY);
        boolean topLeft = isTileSolid(rectX, rectY + collisionRectHeight);
        boolean topRight = isTileSolid(rectX + collisionRectWidth, rectY + collisionRectHeight);

        return bottomLeft || bottomRight || topLeft || topRight;
    }

    private boolean isTileSolid(float x, float y) {
        int cellX = (int) (x / this.tileWidth);
        int cellY = (int) (y / this.tileHeight);

        TiledMapTileLayer.Cell buildingCell = collisionLayerBuildings.getCell(cellX, cellY);
        if (buildingCell != null) return true;

        TiledMapTileLayer.Cell waterCell = collisionLayerWater.getCell(cellX, cellY);
        if (waterCell != null) return true;

        return false;
    }
    public Rectangle getCollisionRect() {
        return collisionRect;
    }

    public void render(SpriteBatch batch) {
        TextureRegion currentFrame = walkAnimation.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, position.x, position.y, dogSpriteWidth, dogSpriteHeight);
    }

    public void dispose() {
        // Get the texture from one of the animation frames to dispose it
        walkAnimation.getKeyFrames()[0].getTexture().dispose();
    }
}
