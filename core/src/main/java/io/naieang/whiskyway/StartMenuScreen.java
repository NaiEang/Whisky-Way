package io.naieang.whiskyway;

import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class StartMenuScreen implements Screen {

    // We need a reference to the main game class to switch screens
    private final WhiskyWayGame game;

    private Stage stage;
    private SpriteBatch batch; // We need a batch to draw the background texture if it's not part of the stage

    // Textures for all our UI elements
    private Texture backgroundTexture;
    private Texture titleTexture;
    private Texture startButtonNormalTexture;
    private Texture startButtonHoverTexture;

    public StartMenuScreen(WhiskyWayGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        // A Stage is where all our UI actors live
        stage = new Stage(new ScreenViewport());
        batch = new SpriteBatch();

        AudioManager.playMusic(AudioManager.windMusic);

        // IMPORTANT: We must tell LibGDX that the Stage will handle input events.
        Gdx.input.setInputProcessor(stage);

        // Load all the images from your assets/ui folder
        backgroundTexture = new Texture("background&button/startgame1.png");
        titleTexture = new Texture("background&button/titlewhiskyway.png");
        startButtonNormalTexture = new Texture("background&button/startb.png");
        startButtonHoverTexture = new Texture("background&button/bhover.png");

        // --- Create Actors (the LibGDX version of JComponents) ---

        // Create the Start Button
        ImageButton.ImageButtonStyle buttonStyle = new ImageButton.ImageButtonStyle();
        buttonStyle.imageUp = new TextureRegionDrawable(startButtonNormalTexture); // Normal state
        buttonStyle.imageOver = new TextureRegionDrawable(startButtonHoverTexture); // Hover state
        ImageButton startButton = new ImageButton(buttonStyle);

        // Create the Title Image
        Image titleImage = new Image(titleTexture);

        // --- Layout ---
        // A Table is used to neatly arrange actors. It's much better than setting coordinates manually.
        Table table = new Table();
        table.setFillParent(true); // Make the table fill the entire stage
        table.center(); // Center the contents of the table

        // Add the title image to the table.
        table.add(titleImage).width(1200).height(800).padBottom(50); // Add padding below the title
        table.row(); // Create a new row
        // Add the start button to the table.
        table.add(startButton).width(700).height(400);

        // Add the table itself to the stage
        stage.addActor(table);


        // --- Add Functionality ---
        // This is the LibGDX equivalent of an ActionListener
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                AudioManager.playSound(AudioManager.clickSound);
                game.setScreen(new StoryScreen(game)); // Go to StoryScreen
                dispose(); // Optional: dispose menu
            }
        });

    }

    @Override
    public void render(float delta) {
        // Clear the screen
        ScreenUtils.clear(0, 0, 0, 1);

        // Draw the background image first, filling the whole screen
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        // Tell the stage to act (handle animations, etc.) and draw itself.
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // When the window is resized, update the stage's viewport.
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        // Clean up all the textures and the stage to prevent memory leaks
        stage.dispose();
        batch.dispose();
        backgroundTexture.dispose();
        titleTexture.dispose();
        startButtonNormalTexture.dispose();
        startButtonHoverTexture.dispose();
    }

    // Other required Screen methods...
    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}
}
