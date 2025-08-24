package io.naieang.whiskyway;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameScreen implements Screen {

    // World rendering
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer mapRenderer;

    // Entities/UI
    private Player player;
    private GameUI gameUI;

    // Map bounds (for camera clamping)
    private int mapWidthInPixels;
    private int mapHeightInPixels;

    // States
    private final int PLAY_STATE = 0;
    private final int PAUSE_STATE = 1;
    private final int DIALOGUE_STATE = 2;

    @Override
    public void show() {
        // --- Camera + world batch ---
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = 0.4f; // your desired zoom
        batch = new SpriteBatch();

        // --- Load map (safe) ---
        String tmxPath = "tiles/whisky_way/CountrysideMap.tmx"; // <- put your TMX here
        if (!Gdx.files.internal(tmxPath).exists()) {
            throw new RuntimeException("TMX not found at: " + tmxPath + " (put it under assets/)");
        }
        tiledMap = new TmxMapLoader().load(tmxPath);
        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        // Map bounds
        MapProperties props = tiledMap.getProperties();
        int mapWidthInTiles = props.get("width", Integer.class);
        int mapHeightInTiles = props.get("height", Integer.class);
        int tileWidthPx = props.get("tilewidth", Integer.class);
        int tileHeightPx = props.get("tileheight", Integer.class);
        mapWidthInPixels = mapWidthInTiles * tileWidthPx;
        mapHeightInPixels = mapHeightInTiles * tileHeightPx;

        // --- Audio (if you have it wired) ---
        try {
            AudioManager.playMusic(AudioManager.gameMusic);
        } catch (Throwable ignored) {
            // Avoid crashing if audio isn't prepared yet
        }

        // --- Player ---
        player = new Player(16 * 23, 16 * 21, tiledMap, mapWidthInPixels, mapHeightInPixels);

        // --- UI (independent batch) ---
        gameUI = new GameUI(
                player,
                Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight(),
                16,             // tile size
                PLAY_STATE,
                PAUSE_STATE,
                DIALOGUE_STATE,
                PLAY_STATE      // start in play
        );

        // Center camera on player
        camera.position.set(player.position.x, player.position.y, 0);
        camera.update();
    }

    @Override
    public void render(float delta) {
        // --- Input: toggle pause (Esc) ---
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            gameUI.gameState = (gameUI.gameState == PLAY_STATE) ? PAUSE_STATE : PLAY_STATE;
        }

            // --- Update world only when playing ---
        if (gameUI.gameState == PLAY_STATE) {
            player.update(delta);
            gameUI.updateTimer();  // update timer only when game is playing
        }

        // --- Update world ---
        if (gameUI.gameState == PLAY_STATE) {
            player.update(delta);
        }

        // --- Input: toggle fullscreen (esc) ---
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (Gdx.graphics.isFullscreen()) {
                Gdx.graphics.setWindowedMode(1280, 720); // pick your preferred window size
            } else {
                Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
            }
        }

        // --- Clear ---
        ScreenUtils.clear(0, 0, 0, 1);

        // --- Camera follows player + clamps to map ---
        camera.position.set(player.position.x, player.position.y, 0);
        float halfW = camera.viewportWidth * camera.zoom * 0.5f;
        float halfH = camera.viewportHeight * camera.zoom * 0.5f;
        camera.position.x = Math.max(halfW, Math.min(mapWidthInPixels - halfW, camera.position.x));
        camera.position.y = Math.max(halfH, Math.min(mapHeightInPixels - halfH, camera.position.y));
        camera.update();

        // --- Render map ---
        mapRenderer.setView(camera);
        mapRenderer.render();

        // --- Render player ---
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        player.render(batch);
        batch.end();

        // --- UI (independent of camera) ---
        gameUI.updateTimer();
        gameUI.draw();
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        safeDispose(mapRenderer);
        safeDispose(tiledMap);
        safeDispose(batch);
        if (player != null) player.dispose();
        if (gameUI != null) gameUI.dispose();
    }

    private void safeDispose(Object o) {
        try {
            if (o instanceof OrthogonalTiledMapRenderer) ((OrthogonalTiledMapRenderer) o).dispose();
            if (o instanceof TiledMap) ((TiledMap) o).dispose();
            if (o instanceof SpriteBatch) ((SpriteBatch) o).dispose();
        } catch (Exception ignore) {}
    }
}
