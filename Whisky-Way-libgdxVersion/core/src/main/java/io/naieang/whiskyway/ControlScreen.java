package io.naieang.whiskyway;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ControlScreen implements Screen {

    private final WhiskyWayGame game;
    private final PauseScreen pauseScreen;

    private SpriteBatch batch;
    private BitmapFont font;
    private Texture controlBackground;
    private Texture arrowImage;

    private String[] options = { "Back" }; // menu option
    private int commandNum = 0;            // selected menu option
    private float arrowSize = 60;          // arrow width & height
    private float arrowX, arrowY;

    public ControlScreen(WhiskyWayGame game, PauseScreen pauseScreen) {
        this.game = game;
        this.pauseScreen = pauseScreen;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2f);

        controlBackground = new Texture("background&button/startgame1.png"); // your background image
        arrowImage = new Texture("background&button/arrow.png");            // arrow texture

        // Position arrow for "Back" option at bottom-center
        arrowX = (Gdx.graphics.getWidth() - arrowSize) / 2f - 50; // shift left for arrow
        arrowY = 100; // distance from bottom
    }

    @Override
public void render(float delta) {
    Gdx.gl.glClearColor(0, 0, 0, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    batch.begin();

    // Draw background
    batch.draw(controlBackground, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

    // --- Draw the CONTROL title ---
    // Save original scale
    float originalScaleX = font.getData().scaleX;
    float originalScaleY = font.getData().scaleY;

    // Scale font for title only
    font.getData().setScale(4f); // adjust 4f for title size
    String title = "CONTROL";
    GlyphLayout titleLayout = new GlyphLayout(font, title);
    float titleX = (Gdx.graphics.getWidth() - titleLayout.width) / 2f;
    float titleY = Gdx.graphics.getHeight() - 50;
    font.setColor(Color.WHITE);
    font.draw(batch, title, titleX, titleY);

    // Reset font scale after title
    font.getData().setScale(originalScaleX, originalScaleY);

    // --- Control instructions centered ---
    String[] controlsLines = {
        "W / UP :   Move Up",
        " S / DOWN :   Move Down",
        "A / LEFT :   Move Left",
        " D / RIGHT :   Move Right",
        " SPACE :   Pause / Back",
        "   ENTER :   Confirm Selection"
    };


    float spacing = 120; // vertical spacing between lines
    float startY = titleY - 130; // start below the title

    for (int i = 0; i < controlsLines.length; i++) {
        String line = controlsLines[i];
        GlyphLayout lineLayout = new GlyphLayout(font, line);
        float x = (Gdx.graphics.getWidth() - lineLayout.width) / 2f;
        float y = startY - (i * spacing);
        font.draw(batch, line, x, y);
    }

    // --- Draw Back option with arrow at bottom ---
    // Scale font only for "Back"
    font.getData().setScale(3.5f); // adjust size for Back
    String backText = "Back";
    GlyphLayout backLayout = new GlyphLayout(font, backText);
    float backX = (Gdx.graphics.getWidth() - backLayout.width) / 2f;
    float backY = 120 + backLayout.height / 2f;
    font.draw(batch, backText, backX, backY);

    // Draw the arrow
    float arrowX = backX - arrowSize - 10;
    float arrowY = 80;
    batch.draw(arrowImage, arrowX, arrowY, arrowSize, arrowSize);

    // Reset font scale again
    font.getData().setScale(originalScaleX, originalScaleY);

    batch.end();

    handleInput();
}



    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ENTER)) {
            game.setScreen(pauseScreen); // return to pause screen
            dispose();
        }
    }

    @Override public void resize(int width, int height) { }
    @Override public void pause() { }
    @Override public void resume() { }
    @Override public void hide() { }
    @Override
    public void dispose() {
        if(batch != null) batch.dispose();
        if(font != null) font.dispose();
        if(controlBackground != null) controlBackground.dispose();
        if(arrowImage != null) arrowImage.dispose();
    }
}
