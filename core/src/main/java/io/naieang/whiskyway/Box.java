package io.naieang.whiskyway;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Rectangle;

public class Box {
    private Texture texture;
    public Rectangle rect; // Public so the Player can easily check it

    public Box(MapObject mapObject, float mapHeight) {
        // Load the visual for the box. Make sure you have a "box.png" in your assets folder!
        texture = new Texture("object/box.png"); // You can change this path

        // Get the raw Tiled properties
        float x = mapObject.getProperties().get("x", Float.class);
        float y = mapObject.getProperties().get("y", Float.class);
        float width = mapObject.getProperties().get("width", Float.class);
        float height = mapObject.getProperties().get("height", Float.class);

        float correctedY = mapHeight - y - height;

        // Create the rectangle with the corrected Y coordinate
        rect = new Rectangle(x, correctedY, width, height);

    }

    public void render(SpriteBatch batch) {
        // Draw the box texture at its rectangle's position
        batch.draw(texture, rect.x, rect.y, rect.width, rect.height);
    }

    public void dispose() {
        texture.dispose();
    }
}
