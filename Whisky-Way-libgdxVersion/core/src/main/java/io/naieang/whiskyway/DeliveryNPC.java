package io.naieang.whiskyway;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Rectangle;

public class DeliveryNPC {
    private Texture texture;
    public Rectangle rect;

public DeliveryNPC(MapObject mapObject, float mapHeight) {
    // This is your cat NPC
    texture = new Texture("object/cat_front.png"); // Make sure this path is correct

    float x = mapObject.getProperties().get("x", Float.class);
    float y = mapObject.getProperties().get("y", Float.class);
    float npcWidth = 32f; // Use your cat sprite's actual width
    float npcHeight = 32f; // Use your cat sprite's actual height

    // --- APPLYING YOUR WORKING FORMULA ---
    // The Tiled 'y' is the bottom, so we subtract the sprite's height
    // to get the bottom-left for drawing.
    rect = new Rectangle(x, y - npcHeight, npcWidth, npcHeight);
}

    public void render(SpriteBatch batch) {
        batch.draw(texture, rect.x, rect.y, rect.width, rect.height);
    }

    public void dispose() {
        texture.dispose();
    }
}
