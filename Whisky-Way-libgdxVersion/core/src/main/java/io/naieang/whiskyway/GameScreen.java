package io.naieang.whiskyway;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameScreen implements Screen {
    // Reference to the main game class to switch screens
    private WhiskyWayGame game;

    // World Rendering
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer mapRenderer;

    // Entities & UI
    private Player player;
    private Array<Box> boxes;
    private Array<DeliveryNPC> npcs;
    private Array<Car> cars;
    private Array<Dog> dogs;
    private Stage pauseStage;
    private Skin pauseSkin;
    private TextButton resumeButton, quitButton;
    private boolean initialized = false;
    private String[] options = { "Music", "SE", "Control", "Back", "End Game" };

    // blank texture for overlay
    private Texture blankTexture;

    // Map boundaries
    private int mapWidthInPixels;
    private int mapHeightInPixels;
    private int commandNum = 0;   // which menu item is selected
    public GameUI gameUI;

    // Game States (constants for clarity)
    public static final int PLAY_STATE = 0;
    public static final int PAUSE_STATE = 1;
    public static final int DIALOGUE_STATE = 2;

    public GameScreen(WhiskyWayGame game) {
        this.game = game;
    }

    @Override
    public void show() {

        if (initialized) return; // prevent reloading map & resetting
        initialized = true;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = 0.4f;
        batch = new SpriteBatch();

        tiledMap = new TmxMapLoader().load("tiles/whisky_way/CountrysideMap.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        MapProperties props = tiledMap.getProperties();
        mapWidthInPixels = props.get("width", Integer.class) * props.get("tilewidth", Integer.class);
        mapHeightInPixels = props.get("height", Integer.class) * props.get("tileheight", Integer.class);

        // --- Your Spawning Logic ---
        boxes = new Array<>();
        npcs = new Array<>();
        cars = new Array<>();
        dogs = new Array<>();
        // Paste your entire working spawning loop here. For example:
        MapLayer objectLayer = tiledMap.getLayers().get("Object");
        MapLayer pathLayer = tiledMap.getLayers().get("Path");
        if (objectLayer != null && pathLayer != null) {
            for (MapObject mapObject : objectLayer.getObjects()) {
                if (mapObject.getProperties().containsKey("type")) {
                    String type = mapObject.getProperties().get("type", String.class);
                    float x = mapObject.getProperties().get("x", Float.class);
                    float y = mapObject.getProperties().get("y", Float.class);
                    if (type.equals("box")) boxes.add(new Box(mapObject, mapHeightInPixels));
                    else if (type.equals("delivery_point")) npcs.add(new DeliveryNPC(mapObject, mapHeightInPixels));
                    else if (type.equals("car")) {
                        String pathName = mapObject.getProperties().get("path", String.class);
                        PolylineMapObject pathObject = (PolylineMapObject) pathLayer.getObjects().get(pathName);
                        if (pathObject != null) cars.add(new Car(x, y, pathObject, mapHeightInPixels));
                    } else if (type.equals("dog")) {
                        String zoneName = mapObject.getProperties().get("zone", String.class);
                        RectangleMapObject zoneObject = (RectangleMapObject) pathLayer.getObjects().get(zoneName);
                        if (zoneObject != null) dogs.add(new Dog(x, y, zoneObject, mapHeightInPixels));
                    }
                }
            }
        }

        AssetManager.playMusic(AssetManager.gameMusic);

        // Player is created AFTER the map is loaded
        player = new Player(16 * 23, 16 * 21, tiledMap, mapWidthInPixels, mapHeightInPixels, this);

        // GameUI is created last
        gameUI = new GameUI(player, PLAY_STATE, PAUSE_STATE, DIALOGUE_STATE, DIALOGUE_STATE);

        // Show intro dialogue
        gameUI.showDialogue("Welcome to Whisky Way! Find the boxes and deliver them. Press [E] to begin.");

        camera.position.set(player.position.x, player.position.y, 0);
        camera.update();
    }

    @Override
public void render(float delta) {
    // --- Toggle pause with SPACE ---
    if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
        if (gameUI.gameState == PLAY_STATE) {
            gameUI.gameState = PAUSE_STATE;
            game.setScreen(new PauseScreen(game, this)); // Switch to PauseScreen
            return; // stop here, PauseScreen will handle its own rendering
        }
    }

    // --- Update Logic based on State ---
    if (gameUI.gameState == PLAY_STATE) {
        player.update(delta, boxes, npcs);
        for (Car car : cars) car.update(delta);
        for (Dog dog : dogs) dog.update(delta);
        gameUI.playTimer += delta;
    } else if (gameUI.gameState == DIALOGUE_STATE) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            gameUI.hideDialogue();
        }
    }

    // Toggle fullscreen with ESC
    if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
        if (Gdx.graphics.isFullscreen()) {
            Gdx.graphics.setWindowedMode(1280, 720);
        } else {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        }
    }

    // --- Rendering ---
    ScreenUtils.clear(0, 0, 0, 1);

    // Camera follows player
    camera.position.set(player.position.x, player.position.y, 0);
    float halfW = camera.viewportWidth * camera.zoom * 0.5f;
    float halfH = camera.viewportHeight * camera.zoom * 0.5f;
    camera.position.x = Math.max(halfW, Math.min(mapWidthInPixels - halfW, camera.position.x));
    camera.position.y = Math.max(halfH, Math.min(mapHeightInPixels - halfH, camera.position.y));
    camera.update();

    // Render map
    mapRenderer.setView(camera);
    mapRenderer.render();

    // Render entities
    batch.setProjectionMatrix(camera.combined);
    batch.begin();
    for (Box box : boxes) box.render(batch);
    for (DeliveryNPC npc : npcs) npc.render(batch);
    for (Car car : cars) car.render(batch);
    for (Dog dog : dogs) dog.render(batch);
    player.render(batch);
    batch.end();

    // UI
    gameUI.update(delta);
    gameUI.draw();
}

    public void showDialogue(String text) {
        gameUI.showDialogue(text);
    }

    public WhiskyWayGame getGame() {
        return this.game;
    }

    @Override
    public void dispose() {
        // Your existing dispose code is fine.
        tiledMap.dispose();
        batch.dispose();
        if (player != null) player.dispose();
        if (gameUI != null) gameUI.dispose();
        if (boxes.size > 0) boxes.first().dispose();
        if (npcs.size > 0) npcs.first().dispose();
        // No need to dispose cars/dogs if their textures are in AssetManager
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
        if (gameUI != null) gameUI.stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
