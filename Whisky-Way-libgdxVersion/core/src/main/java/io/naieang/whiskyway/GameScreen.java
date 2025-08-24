package io.naieang.whiskyway;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameScreen implements Screen {

    private WhiskyWayGame game;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer mapRenderer;

    private Player player;
    private Array<Box> boxes;
    private Array<DeliveryNPC> npcs;
    private Array<Car> cars;
    private Array<Dog> dogs;
    private GameUI gameUI;

    private int mapWidthInPixels;
    private int mapHeightInPixels;

    public static final int PLAY_STATE = 0;
    public static final int PAUSE_STATE = 1;
    public static final int DIALOGUE_STATE = 2;

    public GameScreen(WhiskyWayGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = 0.4f;
        batch = new SpriteBatch();

        tiledMap = new TmxMapLoader().load("tiles/whisky_way/CountrysideMap.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        MapProperties props = tiledMap.getProperties();
        mapWidthInPixels = props.get("width", Integer.class) * props.get("tilewidth", Integer.class);
        mapHeightInPixels = props.get("height", Integer.class) * props.get("tileheight", Integer.class);

        boxes = new Array<>();
        npcs = new Array<>();
        cars = new Array<>();
        dogs = new Array<>();

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

        player = new Player(16 * 23, 16 * 21, tiledMap, mapWidthInPixels, mapHeightInPixels, this);

        // Initialize GameUI with menu system
        gameUI = new GameUI(player, PLAY_STATE, PAUSE_STATE, DIALOGUE_STATE, PLAY_STATE);

        // Show intro dialogue
        showDialogue("Welcome to Whisky Way! Find the boxes and deliver them. Press [E] to begin.");

        camera.position.set(player.position.x, player.position.y, 0);
        camera.update();
    }

    @Override
    public void render(float delta) {
        handleInput();

        // --- Update ---
        if (gameUI.gameState == PLAY_STATE) {
            player.update(delta, boxes, npcs);
            for (Car car : cars) car.update(delta);
            for (Dog dog : dogs) dog.update(delta);
            gameUI.playTimer += delta;
        }

        // --- Render ---
        ScreenUtils.clear(0, 0, 0, 1);
        camera.position.set(player.position.x, player.position.y, 0);
        float halfW = camera.viewportWidth * camera.zoom * 0.5f;
        float halfH = camera.viewportHeight * camera.zoom * 0.5f;
        camera.position.x = Math.max(halfW, Math.min(mapWidthInPixels - halfW, camera.position.x));
        camera.position.y = Math.max(halfH, Math.min(mapHeightInPixels - halfH, camera.position.y));
        camera.update();

        mapRenderer.setView(camera);
        mapRenderer.render();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for (Box box : boxes) box.render(batch);
        for (DeliveryNPC npc : npcs) npc.render(batch);
        for (Car car : cars) car.render(batch);
        for (Dog dog : dogs) dog.render(batch);
        player.render(batch);
        batch.end();

        gameUI.update(delta);
        gameUI.draw();

        if (gameUI.gameState == PAUSE_STATE) {
            batch.begin();
            gameUI.drawOptionScreen(batch);
            batch.end();
        }
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            if (gameUI.gameState == DIALOGUE_STATE) {
                gameUI.hideDialogue();
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if (gameUI.gameState == PLAY_STATE) gameUI.gameState = PAUSE_STATE;
            else if (gameUI.gameState == PAUSE_STATE) gameUI.gameState = PLAY_STATE;
        }

        if (gameUI.gameState == PAUSE_STATE) {
            int maxCommand = 5;
            if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
                gameUI.commandNum--;
                if (gameUI.commandNum < 0) gameUI.commandNum = maxCommand;
                // AssetManager.playSE(5);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
                gameUI.commandNum++;
                if (gameUI.commandNum > maxCommand) gameUI.commandNum = 0;
                // AssetManager.playSE(5);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                handleEnterMenu();
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (Gdx.graphics.isFullscreen()) {
                Gdx.graphics.setWindowedMode(1280, 720);
            } else {
                Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
            }
        }
    }

    private void handleEnterMenu() {
        switch (gameUI.subState) {
            case 0:
                if (gameUI.commandNum == 0) {
                } else if (gameUI.commandNum == 3) {
                    gameUI.subState = 2;
                    gameUI.commandNum = 0;
                } else if (gameUI.commandNum == 5) {
                    gameUI.subState = 3;
                    gameUI.confirmCommandNum = 0;
                }
                break;
            case 2:
                gameUI.subState = 0;
                gameUI.commandNum = 3;
                break;
            case 3:
                if (gameUI.confirmCommandNum == 0) {
                    Gdx.app.exit();
                } else {
                    gameUI.subState = 0;
                    gameUI.commandNum = 5;
                }
                break;
        }
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
        gameUI.stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override
    public void dispose() {
        tiledMap.dispose();
        batch.dispose();
        if (player != null) player.dispose();
        if (gameUI != null) gameUI.dispose();
        if (boxes.size > 0) boxes.first().dispose();
        if (npcs.size > 0) npcs.first().dispose();
    }

    // -------------------------
    // FIX FOR PLAYER CALLS
    // -------------------------
    public void showDialogue(String text) {
        if (gameUI != null) {
            gameUI.showDialogue(text);
        }
    }

    public WhiskyWayGame getGame() {
        return this.game;
    }
}
