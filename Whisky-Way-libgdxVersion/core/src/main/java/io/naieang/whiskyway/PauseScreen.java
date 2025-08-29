package io.naieang.whiskyway;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class PauseScreen implements Screen {
    private WhiskyWayGame game;
    public GameScreen gameScreen;

    private SpriteBatch batch;
    private BitmapFont maruMonicaFont;
    private Texture arrowImage;
    private Texture pauseBackground;  // ✅ background image

    private int commandNum = 0;   // which menu item is selected
    private String[] options = { "Music", "SE", "Control", "Back", "End Game" };

    // Layout variables
    private int tileSize = 32;
    private float frameX = 100;

    public PauseScreen(WhiskyWayGame game, GameScreen gameScreen) {
        this.game = game;
        this.gameScreen = gameScreen;

        batch = new SpriteBatch();
        maruMonicaFont = loadFontSafe("font/x12y16pxMaruMonica.ttf", 28, Color.WHITE);

        // Make text bigger without reloading the font
        maruMonicaFont.getData().setScale(1.5f);

        arrowImage = new Texture("background&button/arrow.png"); 
        pauseBackground = new Texture("background&button/startgame1.png"); // ✅ your pause background image
    }

    /** Safe font loader */
    private BitmapFont loadFontSafe(String path, int size, Color color) {
        try {
            if (Gdx.files.internal(path).exists()) {
                FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal(path));
                FreeTypeFontGenerator.FreeTypeFontParameter p = new FreeTypeFontGenerator.FreeTypeFontParameter();
                p.size = size;
                p.color = color;
                BitmapFont f = gen.generateFont(p);
                gen.dispose();
                return f;
            } else {
                Gdx.app.error("PauseScreen", "Font missing: " + path + " — using default BitmapFont.");
                BitmapFont fallback = new BitmapFont();
                fallback.setColor(color);
                return fallback;
            }
        } catch (Exception e) {
            Gdx.app.error("PauseScreen", "Failed to load font " + path + ": " + e.getMessage());
            BitmapFont fallback = new BitmapFont();
            fallback.setColor(color);
            return fallback;
        }
    }

    @Override
    public void render(float delta) {
        handleInput();

        Gdx.gl.glClearColor(0, 0, 0, 1); // clear screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        // ✅ Draw pause background full screen
        batch.draw(pauseBackground, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // ✅ Draw menu
        drawMenu();

        batch.end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            commandNum--;
            if (commandNum < 0) commandNum = options.length - 1;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.S) || Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            commandNum++;
            if (commandNum >= options.length) commandNum = 0;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            executeCommand();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            game.setScreen(gameScreen);
            gameScreen.gameUI.gameState = GameScreen.PLAY_STATE;
        }
    }

    private void executeCommand() {
        switch (commandNum) {
            case 0:
                System.out.println("Music settings (not implemented)");
                break;
            case 1:
                System.out.println("Sound Effect settings (not implemented)");
                break;
            case 2: // Control
                game.setScreen(new ControlScreen(game, this)); 
                break;

            case 3:
                game.setScreen(gameScreen);
                break;
            case 4:
                Gdx.app.exit();
                break;
        }
    }
    private void drawMenu() {
        // === Draw "OPTION" title ===
        String title = "OPTION";
        GlyphLayout titleLayout = new GlyphLayout(maruMonicaFont, title);
        float titleX = (Gdx.graphics.getWidth() - titleLayout.width) / 2f; // center horizontally
        float titleY = Gdx.graphics.getHeight() - 100; // near top, adjust padding as needed

        maruMonicaFont.draw(batch, title, titleX, titleY);

        // === Draw menu options ===
        float startY = 800;
        float spacing = tileSize * 5f;
        float textX = frameX + tileSize * 8;

        for (int i = 0; i < options.length; i++) {
            String option = options[i];
            GlyphLayout layout = new GlyphLayout(maruMonicaFont, option);
            float textY = startY - (i * spacing);

            maruMonicaFont.draw(batch, option, textX, textY);

            if (commandNum == i) {
                float arrowX = textX - tileSize - 40;
                float arrowY = textY - layout.height - 15;
                batch.draw(arrowImage, arrowX, arrowY, 50, 50);
            }
        }
    }


    @Override public void show() {}
    @Override public void resize(int w, int h) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override
    public void dispose() {
        batch.dispose();
        maruMonicaFont.dispose();
        arrowImage.dispose();
        pauseBackground.dispose(); // ✅ dispose background
    }
}
