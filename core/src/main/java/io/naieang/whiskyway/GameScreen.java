package io.naieang.whiskyway;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import com.badlogic.gdx.utils.viewport.ScreenViewport;

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
    private Array<Car> cars;
    private Array<Dog> dogs;

    private boolean isReady = false;

    private Stage uiStage;
    private Label dialogueLabel;
    private Table dialogueTable;
    private WhiskyWayGame game;

    public enum GameState{
        RUNNING,
        DIALOGUE
    }
    private GameState currentState = GameState.DIALOGUE; //Start in dialogue mode

    public GameScreen(WhiskyWayGame whiskyWayGame){
        this.game = whiskyWayGame;
        camera = new OrthographicCamera();
    }

    // This method is like create() for a screen. It runs once when the screen is shown.
    @Override
    public void show() {
        batch = new SpriteBatch();
        tiledMap = new TmxMapLoader().load("tiles/whisky_way/CountrysideMap.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        MapProperties properties = tiledMap.getProperties();
        mapWidthInPixels = properties.get("width", Integer.class) * properties.get("tilewidth", Integer.class);
        mapHeightInPixels = properties.get("height", Integer.class) * properties.get("tileheight", Integer.class);

        float unitScale = 1f;

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
                    if (type.equals("box")) {
                        boxes.add(new Box(mapObject, mapHeightInPixels));
                    } else if (type.equals("delivery_point")) {
                        npcs.add(new DeliveryNPC(mapObject, mapHeightInPixels));
                    } else if (type.equals("car")) {
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
        camera.setToOrtho(false, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        camera.zoom = 0.4f;

        player = new Player(16* 23, 16*21, tiledMap, mapWidthInPixels, mapHeightInPixels, this);

        camera.position.set(player.position.x, player.position.y, 0);
        camera.update();

        AssetManager.playMusic(AssetManager.gameMusic);
        isReady = true;

        uiStage = new Stage(new ScreenViewport());
        BitmapFont font = new BitmapFont(); // Using the default font
        Label.LabelStyle style = new Label.LabelStyle(font, com.badlogic.gdx.graphics.Color.BLACK);

        dialogueLabel = new Label("Welcome to Whisky Way!\nI need you to find the lost boxes and deliver them.\nPress [E] to continue.", style);
        dialogueLabel.setWrap(true); // Allows text to wrap to the next line

        // Create a table to act as the dialogue box background
        dialogueTable = new Table();
        // To give it a background, we need a drawable. We can create one from a simple white pixel.
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(1, 1, 1, 0.8f)); // White with 80% opacity
        pixmap.fill();
        dialogueTable.setBackground(new TextureRegionDrawable(new Texture(pixmap)));
        pixmap.dispose();

        dialogueTable.add(dialogueLabel).expand().fill().pad(20);
        dialogueTable.setSize(Gdx.graphics.getWidth() * 0.8f, 150); // Box is 80% of screen width, 150px tall
        dialogueTable.setPosition(Gdx.graphics.getWidth() * 0.1f, 20); // Position it at the bottom

        uiStage.addActor(dialogueTable);
    }

    public WhiskyWayGame getGame() {
        return this.game;
    }

    // This is the game loop! It replaces your run() and paintComponent() methods.
    @Override
    public void render(float delta) {
        if(!isReady) return;
        // --- UPDATE LOGIC BASED ON STATE ---
        switch (currentState) {
            case RUNNING:
                // Only update the player and NPCs if the game is in the running state
                player.update(delta, boxes, npcs);
                for(Car car: cars) car.update(delta);
                for(Dog dog: dogs) dog.update(delta);
                break;
            case DIALOGUE:
                // If in dialogue, just check for input to continue
                if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                    currentState = GameState.RUNNING; // Switch to playing the game
                    dialogueTable.setVisible(false); // Hide the dialogue box
                }
                break;
        }

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
        for (Car car: cars){
            car.render(batch);
        }
        for ( Dog dog: dogs){
            dog.render(batch);
        }
//        Gdx.app.log("Player Pos","X:"+player.position.x+" ,Y:"+player.position.y);

        player.render(batch); // Player has its own render method
        batch.end();

        if(currentState == GameState.DIALOGUE){
            uiStage.act(delta);
            uiStage.draw();
        }
    }
    public void showDialogue(String text){
        dialogueLabel.setText(text);
        dialogueTable.setVisible(true);
        currentState = GameState.DIALOGUE;
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
