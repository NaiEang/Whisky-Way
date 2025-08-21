// GameScreen.java
package io.naieang.whiskyway;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameScreen implements Screen {
    // These are the LibGDX equivalents of your GamePanel systems
    private TiledMap tiledMap;
    private TiledMapRenderer tiledMapRenderer;
    private OrthographicCamera camera;
    private SpriteBatch batch;

    // Entity
    private Player player;

    // Map boundaries for camera clamping
    private int mapWidthInPixels;
    private int mapHeightInPixels;

    // This method is like create() for a screen. It runs once when the screen is shown.
    @Override
    public void show() {
        batch = new SpriteBatch();
        tiledMap = new TmxMapLoader().load("tiles/whisky_way/CountrysideMap.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        MapProperties properties = tiledMap.getProperties();
        int mapWidthInTiles = properties.get("width", Integer.class);
        int mapHeightInTiles = properties.get("height", Integer.class);
        int tileWidthInPixels = properties.get("tilewidth", Integer.class);
        int tileHeightInPixels = properties.get("tileheight", Integer.class);
        mapWidthInPixels = mapWidthInTiles * tileWidthInPixels;
        mapHeightInPixels = mapHeightInTiles * tileHeightInPixels;

        // --- CLEAN CAMERA SETUP ---
        camera = new OrthographicCamera();
        // Set the camera's view to the actual screen size first.
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // NOW, apply the zoom to get the desired view.
        camera.zoom = 0.4f; // Adjust this value to get the size you want.

        // Play music
        AudioManager.playMusic(AudioManager.gameMusic);

        // Create the player and pass the map boundaries
        player = new Player(16 * 23, 16 * 21, tiledMap, mapWidthInPixels, mapHeightInPixels);

        // Immediately center the camera on the player
        camera.position.set(player.position.x, player.position.y, 0);

        // Finalize all camera settings
        camera.update();
    }

    // This is the game loop! It replaces your run() and paintComponent() methods.
    @Override
    public void render(float delta) {
        // Equivalent to your update() method
        player.update(delta);

        // Clear the screen
        ScreenUtils.clear(0, 0, 0, 1);

        // Camera follow and clamp logic
        camera.position.set(player.position.x, player.position.y, 0);
        float cameraHalfWidth = camera.viewportWidth * camera.zoom * 0.5f;
        float cameraHalfHeight = camera.viewportHeight * camera.zoom * 0.5f;
        camera.position.x = Math.max(cameraHalfWidth, camera.position.x);
        camera.position.x = Math.min(mapWidthInPixels - cameraHalfWidth, camera.position.x);
        camera.position.y = Math.max(cameraHalfHeight, camera.position.y);
        camera.position.y = Math.min(mapHeightInPixels - cameraHalfHeight, camera.position.y);
        camera.update();

        // Equivalent to your paintComponent() drawing logic
        // 1. Draw the map
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();

        // 2. Draw the player and other entities
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        player.render(batch); // Player has its own render method
        // (Later, you would loop through and render NPCs here too)
        batch.end();
    }

    @Override
    public void dispose() {
        // Clean up all assets
        tiledMap.dispose();
        batch.dispose();
        player.dispose();
    }

    // --- Other required Screen methods ---
    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
    }
    @Override
    public void pause() { }
    @Override
    public void resume() { }
    @Override
    public void hide() { }
}
