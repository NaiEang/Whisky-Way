package io.naieang.whiskyway;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.MathUtils;
import org.w3c.dom.css.Rect;

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

    public Dog(float startX, float startY, RectangleMapObject zoneObject, float mapHeight) {
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

    }

    public void update(float deltaTime) {
        idleTimer -= deltaTime;

        if (position.dst(targetPosition) < 2.0f && idleTimer <= 0) {
            pickNewTarget();
        }

        if (position.dst(targetPosition) > 2.0f) {
            Vector2 direction = targetPosition.cpy().sub(position).nor();
            position.add(direction.scl(speed * deltaTime));
            isMoving = true;
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
        idleTimer = MathUtils.random(2.0f, 5.0f);
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
