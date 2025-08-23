package io.naieang.whiskyway;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Rectangle;

public class DeliveryNPC {
    private Texture texture;
    public Rectangle rect;

    public DeliveryNPC(MapObject mapObject, float mapHeight) {
        // Make sure you have an "npc.png" in your assets folder!
        texture = new Texture("object/cat_front.png"); // You can change this path

        // Create a rectangle for the NPC. Since a "Point" in Tiled has no width/height,
        // we'll give it a default size (e.g., 16x16).
        float x = mapObject.getProperties().get("x", Float.class);
        float y = mapObject.getProperties().get("y", Float.class);
        float defaultSize = 16f;

        // Convert Tiled's top-down Y to LibGDX's bottom-up Y
        float correctedY = mapHeight - y - defaultSize;

        // Create the rectangle with the corrected Y coordinate
        rect = new Rectangle(x, correctedY, defaultSize, defaultSize);
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, rect.x, rect.y, rect.width, rect.height);
    }

    public void dispose() {
        texture.dispose();
    }
}
