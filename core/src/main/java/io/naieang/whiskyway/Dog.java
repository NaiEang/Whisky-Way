package io.naieang.whiskyway;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Dog {
    // --- ANIMATION ---
    private Animation<TextureRegion> walkDownAnimation;
    private Animation<TextureRegion> walkUpAnimation;
    private Animation<TextureRegion> walkLeftAnimation;
    private Animation<TextureRegion> walkRightAnimation;
    private float stateTime;

    // --- TEXTURES (to hold idle frames and for disposal) ---
    private Texture dogFront1, dogFront2, dogBack1, dogBack2, dogLeft1, dogLeft2, dogRight1, dogRight2;

    // --- STATE ---
    private enum State { STANDING, WALKING_UP, WALKING_DOWN, WALKING_LEFT, WALKING_RIGHT }
    private State currentState = State.STANDING;
    private State lastWalkState = State.WALKING_DOWN; // To know which way to face when idle

    // --- SPRITE & POSITION ---
    private float dogSpriteWidth = 32f;  // Adjust this to the actual size you want
    private float dogSpriteHeight = 32f; // Adjust this to the actual size you want
    public Vector2 position;
    private float speed = 50f;

    // --- AI ---
    private Rectangle movementZone;
    private Vector2 targetPosition;

    private boolean isStuck = false;
    private float stuckTimer = 0f;

    private float idleTimer = 0f;
    private boolean isMoving = false;

    // --- COLLISION ---
    private Rectangle collisionRect;
    private TiledMapTileLayer collisionLayerBuildings;
    private TiledMapTileLayer collisionLayerWater;
    private TiledMapTileLayer collisionLayerFence;
    private TiledMapTileLayer collisionLayerPlant;

    private float tileWidth, tileHeight;
    private float collisionRectXOffset = 4f;
    private float collisionRectYOffset = 2f;
    private float collisionRectWidth = 24f;  // Make this slightly smaller than the sprite width
    private float collisionRectHeight = 16f; // Make this smaller than the sprite height

    public Dog(float startX, float startY, RectangleMapObject zoneObject, float mapHeight,
               TiledMapTileLayer buildingsLayer, TiledMapTileLayer waterLayer, TiledMapTileLayer fenceLayer, TiledMapTileLayer plantLayer) {

        // --- LOAD TEXTURES ---
        // Make sure these file paths and names are EXACTLY correct
        dogFront1 = new Texture("NPC/dog_front.png");
        dogFront2 = new Texture("NPC/dog_front1.png");
        dogBack1 = new Texture("NPC/dog_back.png");
        dogBack2 = new Texture("NPC/dog_back1.png");
        dogLeft1 = new Texture("NPC/dog_left.png");
        dogLeft2 = new Texture("NPC/dog_left1.png");
        dogRight1 = new Texture("NPC/dog_right.png");
        dogRight2 = new Texture("NPC/dog_right1.png");

        // --- CREATE ANIMATIONS ---
        walkDownAnimation = new Animation<>(0.3f, new TextureRegion(dogFront1), new TextureRegion(dogFront2));
        walkUpAnimation = new Animation<>(0.3f, new TextureRegion(dogBack1), new TextureRegion(dogBack2));
        walkLeftAnimation = new Animation<>(0.3f, new TextureRegion(dogLeft1), new TextureRegion(dogLeft2));
        walkRightAnimation = new Animation<>(0.3f, new TextureRegion(dogRight1), new TextureRegion(dogRight2));
        stateTime = 0f;

        // --- SET INITIAL POSITION & AI TARGET ---
        position = new Vector2(startX, startY - dogSpriteHeight);
        targetPosition = new Vector2(position); // Start with the target at the current position

        // --- SET MOVEMENT ZONE ---
        Rectangle tiledRect = zoneObject.getRectangle();
        float correctedZoneY = mapHeight - tiledRect.y - tiledRect.height;
        this.movementZone = new Rectangle(tiledRect.x, correctedZoneY, tiledRect.width, tiledRect.height);

        // --- SETUP COLLISION ---
        collisionRect = new Rectangle(position.x, position.y, dogSpriteWidth, dogSpriteHeight);
        this.collisionLayerBuildings = buildingsLayer;
        this.collisionLayerWater = waterLayer;
        this.collisionLayerFence = fenceLayer;
        this.collisionLayerPlant = plantLayer;

        this.tileWidth = buildingsLayer.getTileWidth();
        this.tileHeight = buildingsLayer.getTileHeight();

        pickNewTarget();
    }

// In Dog.java

    // In Dog.java

    // In Dog.java

    public void update(float deltaTime) {
        // If the dog is stuck, it has a special behavior
        if (isStuck) {
            stuckTimer -= deltaTime;
            if (stuckTimer <= 0) {
                isStuck = false; // We're no longer stuck
                pickNewTarget(); // Now pick a new real target
            }
            return; // Do nothing else this frame
        }

        // This is the normal AI logic
        idleTimer -= deltaTime;

        // Decide if we should be moving or idle
        if (position.dst(targetPosition) < 2.0f) { // We have arrived
            isMoving = false;
            if (idleTimer <= 0) {
                pickNewTarget();
            }
        } else {
            isMoving = true;
        }

        // --- MOVEMENT AND COLLISION ---
        if (isMoving) {
            stateTime += deltaTime;

            float oldX = position.x;
            float oldY = position.y;

            Vector2 direction = targetPosition.cpy().sub(position).nor();
            position.add(direction.scl(speed * deltaTime));

            // Check for collision
            if (isCellBlocked(position.x, position.y)) {
                position.set(oldX, oldY); // Revert position

                // --- THIS IS THE KEY FIX: ENTER THE "STUCK" STATE ---
                isStuck = true;
                stuckTimer = 0.5f; // Force a 0.5 second pause
                isMoving = false;  // Stop all movement
                // ----------------------------------------------------
            }
        }

        // --- ANIMATION STATE ---
        if (isMoving) {
            // Determine animation based on direction (your existing logic is correct)
            Vector2 direction = targetPosition.cpy().sub(position).nor(); // Recalculate for accuracy
            if (Math.abs(direction.x) > Math.abs(direction.y)) {
                if (direction.x > 0) currentState = State.WALKING_RIGHT; else currentState = State.WALKING_LEFT;
            } else {
                if (direction.y > 0) currentState = State.WALKING_UP; else currentState = State.WALKING_DOWN;
            }
            lastWalkState = currentState;
        } else {
            currentState = State.STANDING;
        }

        // Update the collision rectangle's final position
        collisionRect.setPosition(position.x, position.y);
    }

    private void pickNewTarget() {
        float newX = MathUtils.random(movementZone.x, movementZone.x + movementZone.width - dogSpriteWidth);
        float newY = MathUtils.random(movementZone.y, movementZone.y + movementZone.height - dogSpriteHeight);
        targetPosition.set(newX, newY);
        idleTimer = MathUtils.random(2.0f, 5.0f);
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

        // Check Buildings
        TiledMapTileLayer.Cell buildingCell = collisionLayerBuildings.getCell(cellX, cellY);
        if (buildingCell != null) return true;

        // Check Water
        TiledMapTileLayer.Cell waterCell = collisionLayerWater.getCell(cellX, cellY);
        if (waterCell != null) return true;

        // --- NEW: Check Fences ---
        if (collisionLayerFence != null) { // Safety check in case the layer doesn't exist on a map
            TiledMapTileLayer.Cell fenceCell = collisionLayerFence.getCell(cellX, cellY);
            if (fenceCell != null) return true;
        }

        // --- NEW: Check Plants ---
        if (collisionLayerPlant != null) { // Safety check
            TiledMapTileLayer.Cell plantCell = collisionLayerPlant.getCell(cellX, cellY);
            if (plantCell != null) return true;
        }

        // If we've checked all layers and found no solid tiles, the way is clear.
        return false;
    }

    public Rectangle getCollisionRect() {
        return collisionRect;
    }

    public void render(SpriteBatch batch) {
        TextureRegion currentFrame = null;
        switch (currentState) {
            case WALKING_UP:
                currentFrame = walkUpAnimation.getKeyFrame(stateTime, true);
                break;
            case WALKING_DOWN:
                currentFrame = walkDownAnimation.getKeyFrame(stateTime, true);
                break;
            case WALKING_LEFT:
                currentFrame = walkLeftAnimation.getKeyFrame(stateTime, true);
                break;
            case WALKING_RIGHT:
                currentFrame = walkRightAnimation.getKeyFrame(stateTime, true);
                break;
            case STANDING:
                // Show the first frame of the last direction walked
                switch (lastWalkState) {
                    case WALKING_UP: currentFrame = new TextureRegion(dogBack1); break;
                    case WALKING_LEFT: currentFrame = new TextureRegion(dogLeft1); break;
                    case WALKING_RIGHT: currentFrame = new TextureRegion(dogRight1); break;
                    default: currentFrame = new TextureRegion(dogFront1); break;
                }
                break;
        }
        if (currentFrame != null) {
            batch.draw(currentFrame, position.x, position.y, dogSpriteWidth, dogSpriteHeight);
        }
    }

    public void dispose() {
        // Dispose all 8 textures
        dogFront1.dispose();
        dogFront2.dispose();
        dogBack1.dispose();
        dogBack2.dispose();
        dogLeft1.dispose();
        dogLeft2.dispose();
        dogRight1.dispose();
        dogRight2.dispose();
    }
}
