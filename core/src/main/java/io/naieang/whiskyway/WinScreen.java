package io.naieang.whiskyway;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.Align;

public class WinScreen implements Screen {
    private Stage stage;
    private WhiskyWayGame game;

    private Texture replayButtonTexture;
    private Texture exitButtonTexture;

    public WinScreen(WhiskyWayGame game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());

        replayButtonTexture = new Texture("background&button/restartb.png");
        exitButtonTexture = new Texture("background&button/exitb.png");

        BitmapFont font = new BitmapFont();
        Label.LabelStyle style = new Label.LabelStyle(font, Color.WHITE);

        Label winLabel = new Label("Congratulations!\nYou completed all deliveries.", style);
        Label newTask = new Label("New Task Coming Soon!", style);
        winLabel.setFontScale(2.5f);
        newTask.setFontScale(3.5f);
        winLabel.setAlignment(Align.center);
        newTask.setPosition(1100,900,Align.center);
        newTask.setWidth(150);
        newTask.setHeight(150);

        ImageButton replayButton = new ImageButton(new TextureRegionDrawable(replayButtonTexture));
        ImageButton exitButton = new ImageButton(new TextureRegionDrawable(exitButtonTexture));

        //Layout
        Table table = new Table();
        table.setFillParent(true);
        table.center();

        table.add(winLabel).padBottom(50);
        table.row();

        Table buttontable = new Table();
        buttontable.add(replayButton).width(250).height(100).padRight(15);
        buttontable.add(exitButton).width(250).height(100).padRight(20);

        table.add(buttontable);

        replayButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Tell the main game class to restart the game
                game.restartGame();
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Exit the application
                Gdx.app.exit();
            }
        });

        table.setFillParent(true);
        table.center();

        table.add(winLabel);
        stage.addActor(table);
        stage.addActor(newTask);
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
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
        replayButtonTexture.dispose();
        exitButtonTexture.dispose();
    }

    // ... (leave resize, pause, resume, hide, dispose as empty methods for now)
}
