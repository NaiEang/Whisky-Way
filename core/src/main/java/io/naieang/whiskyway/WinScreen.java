package io.naieang.whiskyway;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.Align;

public class WinScreen implements Screen {
    private Stage stage;
    private WhiskyWayGame game;

    private Texture replayButtonTexture;
    private Texture exitButtonTexture;
    private Texture endBackgroundTexture;
    private Texture endBackgroundTexture1;

    private SpriteBatch batch;

    public WinScreen(WhiskyWayGame game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        batch = new SpriteBatch();

        replayButtonTexture = new Texture("background&button/restartb.png");
        exitButtonTexture = new Texture("background&button/exitb.png");
        endBackgroundTexture = new Texture("background&button/endgameb1.jpg");
        endBackgroundTexture1 =  new Texture("background&button/endgameb2.jpg");

        BitmapFont font = new BitmapFont();
        Label.LabelStyle style = new Label.LabelStyle(font, Color.WHITE);

        Label winLabel = new Label("Congratulations!\nYou completed all deliveries.", style);
        Label newTask = new Label("New Task Coming Soon!", style);
        winLabel.setFontScale(2.5f);
        newTask.setFontScale(3.5f);
        winLabel.setAlignment(Align.center);

        // Position the "new task" label at top right
        newTask.setPosition(Gdx.graphics.getWidth() , Gdx.graphics.getHeight() - 500, Align.center);

        ImageButton replayButton = new ImageButton(new TextureRegionDrawable(replayButtonTexture));
        ImageButton exitButton = new ImageButton(new TextureRegionDrawable(exitButtonTexture));

        // Layout with table
        Table table = new Table();
        table.setFillParent(true);
        table.center();

        Table newTaskTable = new Table();
        newTaskTable.setFillParent(true);
        newTaskTable.top().center();
        newTaskTable.add(newTask);

        Image background = new Image(endBackgroundTexture);
        background.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        background.setPosition(0, 0);

        table.add(winLabel).padBottom(50).row();

        Table buttonTable = new Table();
        buttonTable.add(replayButton).width(250).height(100).padRight(15).padTop(50);
        buttonTable.add(exitButton).width(250).height(100).padRight(20).padTop(50);

        table.add(buttonTable);

        // Listeners
        replayButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.restartGame();
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        // Add actors
        stage.addActor(background);
        stage.addActor(table);
        stage.addActor(newTaskTable);
    }

    @Override
    public void show() { Gdx.input.setInputProcessor(stage); }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        replayButtonTexture.dispose();
        exitButtonTexture.dispose();
        endBackgroundTexture.dispose();
        batch.dispose();
    }
}
