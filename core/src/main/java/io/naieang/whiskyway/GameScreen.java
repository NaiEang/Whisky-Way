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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;

import io.naieang.whiskyway.Box;
import io.naieang.whiskyway.DeliveryNPC;

public class GameScreen implements Screen {
    // These are the LibGDX equivalents of GamePanel systems
    private TiledMap tiledMap;
    private TiledMapRenderer tiledMapRenderer;
    private OrthographicCamera camera;
    private SpriteBatch batch;

    // Entity
    private Player player;

    // Map boundaries for camera clamping
    private int mapWidthInPixels;
    private int mapHeightInPixels;

    private Array<Box> boxes;
    private Array<DeliveryNPC> npcs;

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

        boxes = new Array<>();
        npcs = new Array<>();

        MapLayer objectLayer = tiledMap.getLayers().get("Object");
        if (objectLayer != null) {
            for (MapObject mapObject : objectLayer.getObjects()) {
                if (mapObject.getProperties().containsKey("type")) {
                    String type = mapObject.getProperties().get("type", String.class);

                    if (type.equals("box")) {
                        boxes.add(new Box(mapObject, mapHeightInPixels));
                    } else if (type.equals("delivery_point")) {
                        npcs.add(new DeliveryNPC(mapObject, mapHeightInPixels));
                    }
                }
            }
        }

        // camera setup
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = 0.4f;

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
        player.update(delta, boxes, npcs);

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


        for (Box box : boxes) {
            box.render(batch);
        }
        for (DeliveryNPC npc : npcs) {
            npc.render(batch);
        }

        player.render(batch); // Player has its own render method
        batch.end();
    }

    @Override
    public void dispose() {
        // Clean up all assets
        tiledMap.dispose();
        batch.dispose();
        player.dispose();

        if (boxes.size > 0) {
            boxes.first().dispose();
        }
        if (npcs.size > 0) {
            npcs.first().dispose();
        }
    }

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
