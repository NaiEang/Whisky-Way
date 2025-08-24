package io.naieang.whiskyway;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.Input;

public class StoryScreen implements Screen {

    private final WhiskyWayGame game;
    private SpriteBatch batch;
    private Texture storyBackground;
    private BitmapFont font;

    public StoryScreen(WhiskyWayGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        font = new BitmapFont(); // Default font, you can use custom if you want
        storyBackground = new Texture("background&button/startgame1.png"); // your story image
    }

    @Override
    public void render(float delta) {
        // Clear screen
        ScreenUtils.clear(0, 0, 0, 1);

        GlyphLayout layout = new GlyphLayout();
        String text = "Meet Leo, a hardworking university student in the bustling town of Whisky Way towngit a.\n" +
            "Tuition is due, bills are piling up, and life at university isn’t cheap.\n" +
            "To survive, Leo has taken a delivery job at the local tavern.\n\n" +
            "But this isn’t just a simple delivery job. Each day, Leo must track down the right people to deliver orders.\n" +
            "Some customers are hiding in far corners, some have tricky requests,\n" +
            "and a few are impossible to find without careful exploration.\n\n" +
            "Every successful delivery earns him precious coins for tuition, textbooks, and survival in university life.\n" +
            "Every missed customer brings frustration—and fewer coins.\n\n" +
            "Leo must explore the town, meet eccentric characters, and find every customer who needs their order,\n" +
            "all while managing time and resources. The streets of Whisky Way are alive with challenges,\n" +
            "and one mistake could cost him dearly.\n\n" +
            "With determination, courage, and a trusty delivery bag, Leo embarks on his mission:\n" +
            "to deliver every order, earn enough for university, and prove that even the smallest delivery can make a big difference.\n\n" +
            "Can Leo deliver every order, earn enough to pay for college tuition, and complete his mission?";
        layout.setText(font, text, Color.WHITE, Gdx.graphics.getWidth(), Align.center,true);

        float textY = (Gdx.graphics.getHeight()+layout.height)/2f;

        batch.begin();
        // Draw a story background
        batch.draw(storyBackground, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Draw story text
        font.getData().setScale(2.3f);
        font.draw(batch, layout, 0, textY);

        font.draw(batch, "Press ENTER to continue...", 100, 100);
        batch.end();

        // Check for input to continue to game
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ENTER)) {
            game.setScreen(new GameScreen(game)); // Go to your GameScreen
            dispose(); // Dispose of story screen
        }
    }

    @Override
    public void resize(int width, int height) { }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    @Override
    public void dispose() {
        if(batch != null) batch.dispose();
        if(storyBackground != null) storyBackground.dispose();
        if(font != null) font.dispose();
    }
}
