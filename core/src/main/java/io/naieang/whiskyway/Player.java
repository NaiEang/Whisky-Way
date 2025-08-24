// Player.java
package io.naieang.whiskyway;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Player {
    // --- SPRITESHEET CONFIGURATION ---
    private static final int FRAME_COLS = 4; // Change this
    private static final int FRAME_ROWS = 4; // Change this to match your directional sprite sheet

    // --- ANIMATION ---
    private Animation<TextureRegion> walkDownAnimation;
    private Animation<TextureRegion> walkUpAnimation;
    private Animation<TextureRegion> walkLeftAnimation;
    private Animation<TextureRegion> walkRightAnimation;
    private float stateTime;

    private float mapBoundaryX;
    private float mapBoundaryY;

    private Texture up1, up2, down1, down2, left1, left2, right1, right2;

    // --- STATE & POSITION ---
    public Vector2 position;
    private float speed = 150f; // Equivalent to your old 'speed = 4' but in pixels/sec

    private enum State { STANDING, WALKING_UP, WALKING_DOWN, WALKING_LEFT, WALKING_RIGHT }
    private State currentState;
    private State lastWalkState= State.WALKING_DOWN;

    // --- COLLISION ---
    private TiledMapTileLayer collisionLayer;
    private TiledMapTileLayer collisionLayer1;
    private TiledMapTileLayer collisionLayer2;
    private TiledMapTileLayer collisionLayer3;

    private float tileWidth, tileHeight;

    private float collisionRectXOffset = 4f;
    private float collisionRectYOffset = 2f;
    private float collisionRectWidth = 8f;
    private float collisionRectHeight = 8f;
    public int ShrimpCount = 3;
    public int coinCount = 0;

    public Player(float x, float y, TiledMap tiledMap, float mapWidth, float mapHeight) {
        position = new Vector2(x, y);
        currentState = State.STANDING;

        this.mapBoundaryX = mapWidth;
        this.mapBoundaryY = mapHeight;

        // Get the collision layer from the map
        this.collisionLayer = (TiledMapTileLayer) tiledMap.getLayers().get("Buildings");
        this.tileWidth = collisionLayer.getTileWidth();
        this.tileHeight = collisionLayer.getTileHeight();

        this.collisionLayer1 = (TiledMapTileLayer) tiledMap.getLayers().get("Water");
        this.tileWidth = collisionLayer1.getTileWidth();
        this.tileHeight = collisionLayer1.getTileHeight();
//
//        this.collisionLayer2 = (TiledMapTileLayer) tiledMap.getLayers().get("toplayer");
//        this.tileWidth = collisionLayer2.getTileWidth();
//        this.tileHeight = collisionLayer2.getTileHeight();
//
//        this.collisionLayer3 = (TiledMapTileLayer) tiledMap.getLayers().get("middlelayer");
//        this.tileWidth = collisionLayer3.getTileWidth();
//        this.tileHeight = collisionLayer3.getTileHeight();

        // 1. Load each of the 8 images as a separate Texture.
        up1 = new Texture("player/up_1.png");
        up2 = new Texture("player/up_2.png");
        down1 = new Texture("player/down_1.png");
        down2 = new Texture("player/down_2.png");
        left1 = new Texture("player/left_1.png");
        left2 = new Texture("player/left_2.png");
        right1 = new Texture("player/right_1.png");
        right2 = new Texture("player/right_2.png");

        // 2. Create animations from the pairs of textures.
        // The first number (e.g., 0.25f) is the time between frames. Lower is faster.
        walkUpAnimation = new Animation<>(0.25f, new TextureRegion(up1), new TextureRegion(up2));
        walkDownAnimation = new Animation<>(0.25f, new TextureRegion(down1), new TextureRegion(down2));
        walkLeftAnimation = new Animation<>(0.25f, new TextureRegion(left1), new TextureRegion(left2));
        walkRightAnimation = new Animation<>(0.25f, new TextureRegion(right1), new TextureRegion(right2));

        stateTime = 0f;
    }

    public void update(float deltaTime) {
        stateTime += deltaTime;

        float oldX = position.x;
        float oldY = position.y;

        boolean isMoving = false;
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            position.y += speed * deltaTime;
            currentState = State.WALKING_UP;
            lastWalkState = currentState; // Remember this direction
            isMoving = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            position.y -= speed * deltaTime;
            currentState = State.WALKING_DOWN;
            lastWalkState = currentState; // Remember this direction
            isMoving = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            position.x -= speed * deltaTime;
            currentState = State.WALKING_LEFT;
            lastWalkState = currentState; // Remember this direction
            isMoving = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            position.x += speed * deltaTime;
            currentState = State.WALKING_RIGHT;
            lastWalkState = currentState; // Remember this direction
            isMoving = true;
        }

        if (!isMoving) {
            currentState = State.STANDING;
        }

        // --- COLLISION LOGIC (No changes here) ---
        if (isCellBlocked(position.x, position.y)) {
            position.x = oldX;
            position.y = oldY;
        }

        // Get the width/height of the player's texture for a more accurate check
        float playerWidth = getFrameWidth();
        float playerHeight = getFrameHeight();

        // Clamp the player's X position
        if (position.x < 0) position.x = 0; // Prevent moving past left edge
        if (position.x + playerWidth > mapBoundaryX) position.x = mapBoundaryX - playerWidth; // Prevent moving past right edge

        // Clamp the player's Y position
        if (position.y < 0) position.y = 0; // Prevent moving past bottom edge
        if (position.y + playerHeight > mapBoundaryY) position.y = mapBoundaryY - playerHeight; // Prevent moving past top edge
    }

    private boolean isCellBlocked(float playerX, float playerY) {
        // Calculate the absolute positions of the collision rectangle's corners
        float rectX = playerX + collisionRectXOffset;
        float rectY = playerY + collisionRectYOffset;

        // Corner 1: Bottom-left of the collision rectangle
        boolean bottomLeft = isTileSolid(rectX, rectY);

        // Corner 2: Bottom-right of the collision rectangle
        boolean bottomRight = isTileSolid(rectX + collisionRectWidth, rectY);

        // Corner 3: Top-left of the collision rectangle
        boolean topLeft = isTileSolid(rectX, rectY + collisionRectHeight);

        // Corner 4: Top-right of the collision rectangle
        boolean topRight = isTileSolid(rectX + collisionRectWidth, rectY + collisionRectHeight);

        // If any corner of the smaller rectangle hits a solid tile, it's a collision.
        return bottomLeft || bottomRight || topLeft || topRight;
    }

    // We will create a new helper method to keep the code clean.
// This method does the actual check for a single point.
    private boolean isTileSolid(float x, float y) {
        // Convert pixel coordinates to tile coordinates
        int cellX = (int) (x / this.tileWidth);
        int cellY = (int) (y / this.tileHeight);

        // Check the Buildings layer
        TiledMapTileLayer.Cell buildingCell = collisionLayer.getCell(cellX, cellY);
        if (buildingCell != null) {
            return true; // Blocked by a building
        }

        // Check the Water layer
        TiledMapTileLayer.Cell waterCell = collisionLayer1.getCell(cellX, cellY);
        if (waterCell != null) {
            return true; // Blocked by water
        }

        // If we checked all layers and found no solid tiles, the way is clear.
        return false;
    }
    private float getFrameWidth() {
        // This is a safe way to get the width of the current texture
        if (walkDownAnimation != null && walkDownAnimation.getKeyFrames().length > 0) {
            return walkDownAnimation.getKeyFrames()[0].getRegionWidth();
        }
        return 0;
    }

    private float getFrameHeight() {
        if (walkDownAnimation != null && walkDownAnimation.getKeyFrames().length > 0) {
            return walkDownAnimation.getKeyFrames()[0].getRegionHeight();
        }
        return 0;
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
                // When standing, show the first frame of the LAST direction we were walking
                switch (lastWalkState) {
                    case WALKING_UP: currentFrame = new TextureRegion(up1); break;
                    case WALKING_DOWN: currentFrame = new TextureRegion(down1); break;
                    case WALKING_LEFT: currentFrame = new TextureRegion(left1); break;
                    case WALKING_RIGHT: currentFrame = new TextureRegion(right1); break;
                }
                break;
        }

        if (currentFrame != null) {
            batch.draw(currentFrame, position.x, position.y);
        }
    }
    public void dispose() {
        up1.dispose();
        up2.dispose();
        down1.dispose();
        down2.dispose();
        left1.dispose();
        left2.dispose();
        right1.dispose();
        right2.dispose();
    }
}
